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
package com.kaadog.kcg.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.kaadog.kcg.core.GeneratorContext.DirectoryConfiguration;
import com.kaadog.kcg.core.GeneratorContext.FileConfiguration;
import com.kaadog.kcg.core.configuration.DataSourceConfiguration;
import com.kaadog.kcg.core.configuration.TransformFunctionConfiguration;
import com.kaadog.kcg.core.configuration.TransformFunctionConfiguration.ClassNameCamelCaseTableFunctionConfiguration;
import com.kaadog.kcg.core.function.ClassNameCamelCaseTableFunction;
import com.kaadog.kcg.core.function.ColumnNameLowerCaseColumnFunction;
import com.kaadog.kcg.core.function.DataTypeLowerCaseColumnFunction;
import com.kaadog.kcg.core.function.FieldNameCamelCaseColumnFunction;
import com.kaadog.kcg.core.function.FieldNameLowerCaseColumnFunction;
import com.kaadog.kcg.core.function.GaussDBCommentTableFunction;
import com.kaadog.kcg.core.function.TableNameLowerCaseTableFunction;
import com.kaadog.kcg.core.mapping.Table;
import com.kaadog.kcg.core.properties.GeneratorProperties;
import com.kaadog.kcg.core.utils.FileUtil;

import cn.hutool.core.util.ObjUtil;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;

/**
 * jdbc 数据源生成器
 */
@Slf4j
public class JDBCDataSourceGenerator implements IGenerator {

    private final GeneratorProperties generatorProperties;

    private TemplateContext           templateContext   = new TemplateContext();
    private DataSourceFactory         dataSourceFactory = new DataSourceFactory();
    private GeneratorContext          generatorContext  = new GeneratorContext();

    private FreemarkerConfiguration   freemarkerConfiguration;

    private List<Exception>           exceptions        = new ArrayList<>();

    public JDBCDataSourceGenerator(GeneratorProperties generatorProperties) {
        this.generatorProperties = generatorProperties;
    }

    @Override
    public String generate() throws Throwable {

        initialization();

        // 获取目录配置信息
        List<DirectoryConfiguration> dcs = generatorContext.getDirectoryConfigurations();

        dcs.forEach(dc -> {

            // 创建目录，如果目录存在不会重复创建
            FileUtil.mkdir(dc.getFinalPath());

            try {
                // 文件型必须重新进行 TemplateLoader 设置
                FileTemplateLoader fileTemplateLoader = new FileTemplateLoader(FileUtil.newFile(dc.getOriginalPath()));
                freemarkerConfiguration.getConfiguration().setTemplateLoader(fileTemplateLoader);
            } catch (IOException e) {
                exceptions.add(e);
                log.error("模板名称为：" + dc.getOriginalPath() + " 的 FileTemplateLoader 对象不存在", e);
            }

            List<FileConfiguration> fcs = dc.getFileConfigurations();

            fcs.forEach(fc -> {
                String fileName = fc.getFileName();
                // 当前为模板文件时进行内容替换
                if (fileName.endsWith(generatorProperties.getTemplate().getFileSuffix())) {
                    try {
                        // 根据文件名称获取 Template
                        Template template = freemarkerConfiguration.getConfiguration().getTemplate(fileName);

                        String finalPath = fc.getFinalPath();
                        // 以后缀结尾的正则
                        String regex = generatorProperties.getTemplate().getFileSuffix() + "$";
                        // 替换后缀为空，比如说 User.java.flt 替换后变为 User.java
                        String filePath = Pattern.compile(regex).matcher(finalPath).replaceAll("");
                        String templateFileEncoding = generatorProperties.getTemplate().getFileEncoding();
                        // 判断是否存在将要生成的文件
                        Writer writer = new OutputStreamWriter(new FileOutputStream(filePath), templateFileEncoding);
                        template.process(fc.getData(), writer);
                        writer.close();
                    } catch (TemplateException | IOException e) {
                        exceptions.add(e);
                        log.error("模板名称为：" + fileName + " 执行内容替换失败", e);
                    }
                } else {
                    if (!FileUtil.exist(fc.getFinalPath())) {
                        FileUtil.copy(fc.getOriginalPath(), fc.getFinalPath(), true);
                    }
                }
            });
        });

        String path = templateContext.getFinalOutRootFolder();
        log.info("生成文件成功，可以通过 [{}] 获取", path);
        return path;
    }

    private void initialization() {
        buildTemplateContext();
        buildDataSourceFactory();
        buildGeneratorContext();

        createFreemarkerConfiguration();
    }

    private void buildTemplateContext() {
        templateContext.setTemplateConfiguration(generatorProperties.getTemplate());
    }

    private void buildDataSourceFactory() {
        dataSourceFactory.setDialectConfiguration(generatorProperties.getDialect());
        dataSourceFactory.setDataSourceConfigurations(generatorProperties.getDataSources());

        TransformFunctionConfiguration transformFunctionConfiguration = generatorProperties.getTransformFunction();
        if (transformFunctionConfiguration.getClassNameCamelCaseTableFunction().getEnabled()) {
            ClassNameCamelCaseTableFunction classNameCamelCaseTableFunction = new ClassNameCamelCaseTableFunction();
            ClassNameCamelCaseTableFunctionConfiguration config = transformFunctionConfiguration
                    .getClassNameCamelCaseTableFunction();
            classNameCamelCaseTableFunction.setSearchStrs(config.getSearchStrs());
            dataSourceFactory.addTableFunction(classNameCamelCaseTableFunction);
        }
        if (transformFunctionConfiguration.getColumnNameLowerCaseColumnFunction().getEnabled()) {
            dataSourceFactory.addColumnFunction(new ColumnNameLowerCaseColumnFunction());
        }
        if (transformFunctionConfiguration.getDataTypeLowerCaseColumnFunction().getEnabled()) {
            dataSourceFactory.addColumnFunction(new DataTypeLowerCaseColumnFunction());
        }
        if (transformFunctionConfiguration.getFieldNameCamelCaseColumnFunction().getEnabled()) {
            dataSourceFactory.addColumnFunction(new FieldNameCamelCaseColumnFunction());
        }
        if (transformFunctionConfiguration.getFieldNameLowerCaseColumnFunction().getEnabled()) {
            dataSourceFactory.addColumnFunction(new FieldNameLowerCaseColumnFunction());
        }
        if (transformFunctionConfiguration.getGaussDbCommentTableFunction().getEnabled()) {
            dataSourceFactory.addTableFunction(new GaussDBCommentTableFunction());
        }
        if (transformFunctionConfiguration.getTableNameLowerCaseTableFunction().getEnabled()) {
            dataSourceFactory.addTableFunction(new TableNameLowerCaseTableFunction());
        }
    }

    /**
     * 构建 {@link GeneratorContext} 内容
     * <p>
     * dataSources 数据
     * <ol>
     * <li>dataSources: 所有数据源信息 {@link DataSourceConfiguration}</li>
     * </ol>
     * <p>
     * <p>
     * tables 数据
     * <ol>
     * <li>tables: 所有的表数据{@link Table}</li>
     * </ol>
     * <p>
     * 每一个 table 包含的数据 key
     * <ol>
     * <li>catalog: 数据库 catalog</li>
     * <li>schema: 数据库 schema</li>
     * <li>tableType: 表类型</li>
     * <li>tableName: 表名称</li>
     * <li>tableComment: 表注释类型</li>
     * <li>className: 生成的类名</li>
     * <li>classNameLastLowercase: 生成的类名，首字母小写</li>
     * <li>columns: 表包含的列集合</li>
     * <li>indexs: 表包含的索引集合</li>
     * <li>primaryKeys: 表包含的主键集合</li>
     * <li>foreignKeys: 表包含的外键集合</li>
     * <li>imports: 需要导入的依赖集合</li>
     * </ol>
     */
    private void buildGeneratorContext() {
        generatorContext.addDirectoryConfigurations(templateContext.getDirectoryConfigurations());

        List<Table> tables = dataSourceFactory.getTables();

        // 全局数据
        Map<String, Object> globalData = new HashMap<>();
        globalData.putAll(generatorProperties.getProperties());

        // 数据项
        List<Map<String, Object>> dataMaps = new ArrayList<>();

        tables.forEach(table -> {
            Map<String, Object> data = new HashMap<>();

            // 所有数据源配置信息
            data.put("dataSources", dataSourceFactory.getDataSourceConfigurations());
            // 所有的表数据
            data.put("tables", tables);

            data.put("catalog", table.getCatalog());
            data.put("schema", table.getSchema());
            data.put("tableType", table.getTableType().name());
            data.put("tableName", table.getTableName());
            data.put("tableComment", table.getTableComment());
            data.put("className", table.getClassName());
            data.put("classNameLastLowercase", table.getClassNameLastLowercase());

            data.put("columns", table.getColumns());
            data.put("indexs", table.getColumns());
            data.put("primaryKeys", table.getPrimaryKeys());
            data.put("foreignKeys", table.getForeignKeys());

            Set<String> imports = table.getColumns().stream().filter(column -> {
                return ObjUtil.isNotEmpty(column.getFieldType().getClassName());
            }).map(column -> {
                return column.getFieldType().getClassName();
            }).collect(Collectors.toSet());

            data.put("imports", imports);
            dataMaps.add(data);
        });

        generatorContext.putGlobalData(globalData);
        generatorContext.putDatas(dataMaps);
    }

    /**
     * 创建 {@link FreemarkerConfiguration}
     * <p>
     * 执行以下操作:
     * <ol>
     * <li>创建 {@link FreemarkerConfiguration}</li>
     * <li>生成文件夹、文件 {@link StringTemplateLoader}</li>
     * <li>按照 {@link GeneratorContext} 中的数据进行 {@link GeneratorContext} 中的 directorys 设置</li>
     * </ol>
     */
    private void createFreemarkerConfiguration() {
        freemarkerConfiguration = new FreemarkerConfiguration();
        freemarkerConfiguration.getConfiguration()
                .setDefaultEncoding(generatorProperties.getTemplate().getFileEncoding());

        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();

        List<DirectoryConfiguration> dcs = generatorContext.getDirectoryConfigurations();

        dcs.forEach(dc -> {
            // 设置目录 TemplateLoader
            stringTemplateLoader.putTemplate(templateContext.templateNameFormat(dc.getOriginalPath()),
                                             dc.getOriginalPath());

            // 设置文件目录 TemplateLoader
            List<FileConfiguration> fileConfigurations = dc.getFileConfigurations();
            fileConfigurations.forEach(fc -> {
                stringTemplateLoader.putTemplate(templateContext.templateNameFormat(fc.getOriginalPath()),
                                                 fc.getOriginalPath());
            });
        });

        freemarkerConfiguration.addTemplateLoader(stringTemplateLoader);

        List<Map<String, Object>> dataMaps = generatorContext.getAvailableDataMaps();

        // 目标存放文件夹
        String finalFolderPath = templateContext.getTemplateConfiguration().getTempFolderPath();
        // 生成后输出到的目录
        String outRootFolder = templateContext.getTemplateConfiguration().getOutRootFolder();

        // 目录、目录中的文件会依据数据扩增，这个对象存在最终的配置
        List<DirectoryConfiguration> directoryConfigurations = new ArrayList<>();
        // 遍历数据
        dataMaps.forEach(data -> {
            // 遍历目录
            dcs.forEach(dc -> {

                DirectoryConfiguration directoryConfiguration = new DirectoryConfiguration();

                // 模板中的配置信息
                List<FileConfiguration> fcs = dc.getFileConfigurations();
                // 填充数据后的配置信息
                List<FileConfiguration> fileConfigurations = directoryConfiguration.getFileConfigurations();

                fcs.forEach(fc -> {
                    // 使用数据替换后的路径
                    String fileOut = templateProcessForName(fc.getOriginalPath(), data);
                    String fileName = new File(fc.getOriginalPath()).getName();
                    // 目标存放文件夹
                    String finalPath = FileUtil.replacePath(fileOut, finalFolderPath, outRootFolder);

                    FileConfiguration fileConfiguration = new FileConfiguration();
                    fileConfiguration.setFileName(fileName);
                    fileConfiguration.setOriginalPath(fc.getOriginalPath());
                    fileConfiguration.setFinalPath(finalPath);
                    fileConfiguration.setData(data);

                    fileConfigurations.add(fileConfiguration);
                });

                // 使用数据替换后的路径
                String directoryOut = templateProcessForName(dc.getOriginalPath(), data);
                // 最终的路径
                String finalPath = FileUtil.replacePath(directoryOut, finalFolderPath, outRootFolder);

                String zipOrJarTempFolderName = templateContext.getZipOrJarTempFolderName();

                // 最终解压后的临时模板文件夹
                String finalTempFolderPath = directoryOut
                        .substring(0,
                                   directoryOut.lastIndexOf(zipOrJarTempFolderName) + zipOrJarTempFolderName.length());
                templateContext.setFinalTempFolderPath(finalTempFolderPath);

                // 最终生成文件输出文件夹
                String finalOutRootFolder = finalPath
                        .substring(0, finalPath.lastIndexOf(zipOrJarTempFolderName) + zipOrJarTempFolderName.length());
                templateContext.setFinalOutRootFolder(finalOutRootFolder);

                directoryConfiguration.setOriginalPath(dc.getOriginalPath());
                directoryConfiguration.setFinalPath(finalPath);
                directoryConfiguration.setData(data);

                directoryConfigurations.add(directoryConfiguration);
            });
        });

        // 更新生成器上下文中的目录配置信息
        generatorContext.setDirectoryConfigurations(directoryConfigurations);
    }

    /**
     * 使用 Template 生成目录与文件名称
     */
    private String templateProcessForName(String path, Map<String, Object> data) {
        // 获取目录 Template 并进行数据替换操作
        try {
            Template directoryTemplate = freemarkerConfiguration.getConfiguration()
                    .getTemplate(templateContext.templateNameFormat(path));
            StringWriter directoryOut = new StringWriter();
            directoryTemplate.process(data, directoryOut);
            directoryOut.close();

            log.info("路径 {} -> {}", path, directoryOut);

            return String.valueOf(directoryOut);
        } catch (TemplateException | IOException e) {
            exceptions.add(e);
            log.error("模板名称为：" + path + " 执行路径替换失败", e);
        }
        return path;
    }

    public List<String> getDirectoryPaths() {
        return FileUtil.getDirectoryPaths(generatorProperties.getTemplate().getFolderPath());
    }

    public GeneratorProperties getGeneratorProperties() {
        return generatorProperties;
    }

    public DataSourceFactory getDataSourceFactory() {
        return dataSourceFactory;
    }

    public FreemarkerConfiguration getFreemarkerConfiguration() {
        return freemarkerConfiguration;
    }

    public GeneratorContext getGeneratorContext() {
        return generatorContext;
    }

    @Override
    public List<Exception> exceptions() {
        return exceptions;
    }
}
