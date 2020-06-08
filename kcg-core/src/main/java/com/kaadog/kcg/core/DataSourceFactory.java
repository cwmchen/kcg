/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kaadog.kcg.core;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.kaadog.kcg.core.configuration.DataSourceConfiguration;
import com.kaadog.kcg.core.configuration.DialectConfiguration;
import com.kaadog.kcg.core.configuration.DialectConfiguration.TypeMappingConfiguration;
import com.kaadog.kcg.core.enums.JdbcEnum;
import com.kaadog.kcg.core.enums.TableTypeEnum;
import com.kaadog.kcg.core.exception.DataSourceFactoryException;
import com.kaadog.kcg.core.function.TransformFunction;
import com.kaadog.kcg.core.mapping.Column;
import com.kaadog.kcg.core.mapping.PrimaryKey;
import com.kaadog.kcg.core.mapping.Table;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataSourceFactory {

    /** 方言配置 */
    private DialectConfiguration                              dialectConfiguration;

    /** 针对于 {@link Table} 数据进行在加工 */
    private List<TransformFunction<Connection, List<Table>>>  tableFunctions           = new ArrayList<>(5);

    /** 针对于 {@link Column} 数据进行在加工 */
    private List<TransformFunction<Connection, List<Column>>> columnFunctions          = new ArrayList<>(5);

    /** 数据源配置 */
    private List<DataSourceConfiguration>                     dataSourceConfigurations = new ArrayList<>(1);

    /**
     * 创建一个数据源连接对象
     * 
     * @param dataSourceConfiguration 数据源配置
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private Connection createConnection(DataSourceConfiguration dataSourceConfiguration) throws SQLException,
                                                                                         InstantiationException,
                                                                                         IllegalAccessException,
                                                                                         ClassNotFoundException {

        if (StringUtils.isBlank(dataSourceConfiguration.getDriverClassName())) {
            JdbcEnum jdbcEnum = JdbcEnum.getJdbcEnum(dataSourceConfiguration.getUrl());
            if (Objects.isNull(jdbcEnum)) {
                throw new IllegalArgumentException("jdbcEnum must not be null");
            }

            dataSourceConfiguration.setDriverClassName(jdbcEnum.getDriverClassName());
        }
        log.info("开始加载数据库驱动 {}", dataSourceConfiguration.getDriverClassName());
        Class.forName(dataSourceConfiguration.getDriverClassName()).newInstance();
        log.info("成功加载数据库驱动 {}", dataSourceConfiguration.getDriverClassName());

        Properties properties = new Properties();
        properties.put("remarksReporting", true);
        properties.put("remarks", true);
        properties.put("useInformationSchema", "true");
        properties.put("user", dataSourceConfiguration.getUsername());
        properties.put("password", dataSourceConfiguration.getPassword());

        properties.putAll(dataSourceConfiguration.getProperties());

        log.info("开始创建数据库连接 {} - {}", dataSourceConfiguration.getUrl(), properties.toString());
        Connection connection = DriverManager.getConnection(dataSourceConfiguration.getUrl(), properties);
        log.info("成功创建数据库连接 {} - {}", dataSourceConfiguration.getUrl(), properties.toString());
        return connection;
    }

    public List<Table> getTables() {

        List<Table> tables = new ArrayList<>();
        try {
            for (int i = 0; i < getDataSourceConfigurations().size(); i++) {
                DataSourceConfiguration dsc = getDataSourceConfigurations().get(i);

                Connection connection = createConnection(dsc);
                DatabaseMetaData databaseMetaData = connection.getMetaData();

                String catalog = dsc.getCatalog();
                String schema = dsc.getSchema();

                // 从连接对象获取 catalog
                if (StringUtils.isBlank(catalog)) {
                    catalog = connection.getCatalog();
                }
                // 从连接对象获取 schema
                if (StringUtils.isBlank(schema)) {
                    schema = connection.getSchema();
                }

                // 未指定加载表名称时获取所有的表名称
                if (CollectionUtils.isEmpty(dsc.getTableNames())) {
                    ResultSet rs = databaseMetaData.getTables(catalog, schema, null,
                                                              dsc.getTypes().stream().map(type -> {
                                                                  return type.name();
                                                              }).toArray(String[]::new));
                    while (rs.next()) {
                        dsc.getTableNames().add(rs.getString("TABLE_NAME"));
                    }
                    log.info("未指定加载表，默认进行全库表加载 size={},tableNames={}", dsc.getTableNames().size(), dsc.getTableNames());
                }

                log.info("加载表数据，待加载数 size={},tableNames={}", dsc.getTableNames().size(), dsc.getTableNames());
                for (String tableName : dsc.getTableNames()) {

                    if (StringUtils.isBlank(tableName)) {
                        continue;
                    }

                    Table table = new Table();
                    ResultSet tableResultSet = databaseMetaData.getTables(dsc.getCatalog(), dsc.getSchema(), tableName,
                                                                          dsc.getTypes().stream().map(type -> {
                                                                              return type.name();
                                                                          }).toArray(String[]::new));

                    while (tableResultSet.next()) {
                        TableTypeEnum tableType = TableTypeEnum.valueOf(tableResultSet.getString("TABLE_TYPE"));
                        String remarks = StringUtils.isBlank(tableResultSet.getString("REMARKS")) ? "" : tableResultSet
                                .getString("REMARKS");

                        table.setCatalog(catalog);
                        table.setSchema(schema);
                        table.setTableName(tableName);
                        table.setTableType(tableType);
                        table.setTableComment(remarks);
                        // 类名默认使用表名
                        table.setClassName(table.getTableName());
                    }

                    // 当前表类型不在加载范围内
                    if (!dsc.getTypes().contains(table.getTableType())) {
                        continue;
                    }

                    ResultSet columnResultSet = databaseMetaData.getColumns(catalog, schema, tableName, null);
                    while (columnResultSet.next()) {

                        String columnName = columnResultSet.getString("COLUMN_NAME");
                        String dataType = columnResultSet.getString("TYPE_NAME");
                        String columnComment = StringUtils.isBlank(columnResultSet
                                .getString("REMARKS")) ? "" : columnResultSet.getString("REMARKS");
                        String ordinalPosition = columnResultSet.getString("ORDINAL_POSITION");

                        Column column = new Column();
                        column.setCatalog(catalog);
                        column.setSchema(schema);
                        column.setTableName(tableName);
                        column.setColumnName(columnName);
                        column.setDataType(dataType);
                        column.setOrdinalPosition(ordinalPosition);
                        column.setColumnComment(columnComment);

                        column.setFieldName(column.getColumnName());
                        column.setFieldType(getTypeMappingConfiguration(column.getDataType()));

                        table.addColumn(column);
                    }

                    ResultSet primaryKeyResultSet = databaseMetaData.getPrimaryKeys(catalog, schema, tableName);
                    while (primaryKeyResultSet.next()) {

                        String columnName = primaryKeyResultSet.getString("COLUMN_NAME");
                        String pkName = primaryKeyResultSet.getString("PK_NAME");

                        PrimaryKey primaryKey = new PrimaryKey();
                        primaryKey.setCatalog(catalog);
                        primaryKey.setSchema(schema);
                        primaryKey.setTableName(tableName);
                        primaryKey.setColumnName(columnName);
                        primaryKey.setPkName(pkName);

                        table.addPrimaryKey(primaryKey);
                    }

                    tables.add(table);
                }

                if (CollectionUtils.isNotEmpty(this.tableFunctions)) {
                    log.info("tableFunctions 函数处理，size={}", this.tableFunctions.size());
                    this.tableFunctions.sort(Comparator.comparing(TransformFunction::getOrder));
                    this.tableFunctions.forEach(tableFunction -> {
                        tableFunction.accept(connection, tables);
                    });
                }

                if (CollectionUtils.isNotEmpty(this.columnFunctions)) {
                    log.info("columnFunctions 函数处理，size={}", this.columnFunctions.size());
                    tables.forEach(table -> {
                        this.columnFunctions.sort(Comparator.comparing(TransformFunction::getOrder));
                        this.columnFunctions.forEach(columnFunction -> {
                            columnFunction.accept(connection, table.getColumns());
                        });
                    });
                }
            }

        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
            throw new DataSourceFactoryException(e);
        }

        return tables;
    }

    /**
     * 根据数据库类型获取指定的类型配置信息
     */
    public TypeMappingConfiguration getTypeMappingConfiguration(String dataType) {
        TypeMappingConfiguration typeMappingConfiguration = dialectConfiguration.getTypeMappingConfiguration(dataType);
        if (Objects.isNull(typeMappingConfiguration)) {
            log.info("{} 没有合适的类型", dataType);
            typeMappingConfiguration = new TypeMappingConfiguration("varchar", "String");
        }
        return typeMappingConfiguration;
    }

    public DataSourceFactory addTableFunction(TransformFunction<Connection, List<Table>> tableFunction) {
        this.tableFunctions.add(tableFunction);
        return this;
    }

    public DataSourceFactory addColumnFunction(TransformFunction<Connection, List<Column>> columnFunction) {
        this.columnFunctions.add(columnFunction);
        return this;
    }

    public DataSourceFactory setDataSourceConfigurations(List<DataSourceConfiguration> dataSourceConfigurations) {
        if (CollectionUtils.isEmpty(dataSourceConfigurations)) {
            return this;
        }
        this.dataSourceConfigurations = dataSourceConfigurations;
        return this;
    }

    public DataSourceFactory addDataSourceConfiguration(DataSourceConfiguration dataSourceConfiguration) {
        this.dataSourceConfigurations.add(dataSourceConfiguration);
        return this;
    }

    public List<DataSourceConfiguration> getDataSourceConfigurations() {
        return dataSourceConfigurations;
    }

    public DialectConfiguration getDialectConfiguration() {
        return dialectConfiguration;
    }

    public void setDialectConfiguration(DialectConfiguration dialectConfiguration) {
        this.dialectConfiguration = dialectConfiguration;
    }

}
