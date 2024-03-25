/*
 * Copyright 2020-2024 the original author or authors.
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
package com.kaadog.kcg.core.function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import com.huawei.gauss.jdbc.GaussConnection;
import com.kaadog.kcg.core.enums.JdbcEnum;
import com.kaadog.kcg.core.mapping.Table;

import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.ObjUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * GaussDB 表备注信息获取，同时包含列的备注信息
 */
@Slf4j
public class GaussDBCommentTableFunction implements TransformFunction<Connection, List<Table>> {

    @Override
    public void accept(Connection c, List<Table> tables) {
        if (!ClassLoaderUtil.isPresent(JdbcEnum.GAUSS100.getDriverClassName(), this.getClass().getClassLoader())) {
            return;
        }
        if (!(c instanceof GaussConnection)) {
            return;
        }

        try {
            PreparedStatement ps = c.prepareStatement(getSql(c.getMetaData().getUserName()));
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                String columnName = resultSet.getString("COLUMN_NAME");
                String remarks = resultSet.getString("REMARKS");

                // 设置 table 备注信息
                if (ObjUtil.isEmpty(columnName)) {
                    tables.stream().filter(table -> {
                        return table.getTableName().equalsIgnoreCase(tableName);
                    }).findFirst().map(table -> {
                        table.setTableComment(remarks);
                        return table;
                    });

                } else {
                    // 设置 column 备注信息
                    tables.stream().filter(table -> {
                        return table.getTableName().equalsIgnoreCase(tableName);
                    }).findFirst().map(table -> {
                        table.getColumns().stream().filter(column -> {
                            return column.getColumnName().equalsIgnoreCase(columnName);
                        }).map(column -> {
                            column.setColumnComment(remarks);
                            return column;
                        }).collect(Collectors.toList());

                        return table;
                    });
                }
            }

        } catch (SQLException e) {
            log.error("", e);
        }
    }

    private String getSql(String userName) {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from (");
        sql.append("    select u.name as user_name,t.name as table_name,cl.name as column_name,c.text as remarks from sys.sys_comments c");
        sql.append("        inner join sys.sys_tables t on c.table#=t.id");
        sql.append("        inner join sys.sys_columns cl on t.id=cl.table# and cl.id=c.column#");
        sql.append("        inner join sys.sys_users u on t.user#=u.id and cl.user#=u.id");
        sql.append("        where t.recycled=0 and cl.id=c.column#");
        sql.append("    union all");
        sql.append("    select u.name as user_name,t.name as table_name,'' as column_name,c.text as remarks from sys.sys_comments c");
        sql.append("        inner join sys.sys_tables t on c.table#=t.id");
        sql.append("        inner join sys.sys_users u on t.user#=u.id");
        sql.append("        where t.recycled=0 and c.column# is null");
        sql.append(") t where t.user_name='" + userName + "' order by t.table_name,t.column_name desc");
        return sql.toString();
    }

    @Override
    public int getOrder() {
        return DEFAULT_ORDER - 10;
    }
}
