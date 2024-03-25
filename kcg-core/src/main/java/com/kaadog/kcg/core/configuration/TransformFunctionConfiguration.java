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
package com.kaadog.kcg.core.configuration;

import java.util.List;

import com.kaadog.kcg.core.function.ClassNameCamelCaseTableFunction;
import lombok.Getter;
import lombok.Setter;

/**
 * 数据转换接口配置
 */
@Getter
@Setter
public class TransformFunctionConfiguration {

    /** 类名使用驼峰命名函数配置 */
    private ClassNameCamelCaseTableFunctionConfiguration   classNameCamelCaseTableFunction   = new ClassNameCamelCaseTableFunctionConfiguration();
    /** 列名称转换为小写函数配置 */
    private ColumnNameLowerCaseColumnFunctionConfiguration columnNameLowerCaseColumnFunction = new ColumnNameLowerCaseColumnFunctionConfiguration();
    /** 数据类型转换为小写函数配置 */
    private DataTypeLowerCaseColumnFunctionConfiguration   dataTypeLowerCaseColumnFunction   = new DataTypeLowerCaseColumnFunctionConfiguration();
    /** 列的属性名称使用驼峰命名函数配置 */
    private FieldNameCamelCaseColumnFunctionConfiguration  fieldNameCamelCaseColumnFunction  = new FieldNameCamelCaseColumnFunctionConfiguration();
    /** 列的属性名称转换为小写函数配置 */
    private FieldNameLowerCaseColumnFunctionConfiguration  fieldNameLowerCaseColumnFunction  = new FieldNameLowerCaseColumnFunctionConfiguration();
    /** GaussDB 表备注信息获取函数配置 */
    private GaussDBCommentTableFunctionConfiguration       gaussDbCommentTableFunction       = new GaussDBCommentTableFunctionConfiguration();
    /** 表名称转换为小写函数配置 */
    private TableNameLowerCaseTableFunctionConfiguration   tableNameLowerCaseTableFunction   = new TableNameLowerCaseTableFunctionConfiguration();

    @Getter
    @Setter
    public static class ClassNameCamelCaseTableFunctionConfiguration {

        private Boolean      enabled    = Boolean.TRUE;
        /** 搜索的字符串 */
        private List<String> searchStrs = ClassNameCamelCaseTableFunction.DEFAULT_SEARCH_STRS;
    }

    /**
     * 类名使用驼峰命名函数配置
     */
    @Getter
    @Setter
    public static class ColumnNameLowerCaseColumnFunctionConfiguration {

        private Boolean enabled = Boolean.TRUE;
    }

    /**
     * 数据类型转换为小写函数配置
     */
    @Getter
    @Setter
    public static class DataTypeLowerCaseColumnFunctionConfiguration {

        private Boolean enabled = Boolean.TRUE;
    }

    /**
     * 列的属性名称使用驼峰命名函数配置
     */
    @Getter
    @Setter
    public static class FieldNameCamelCaseColumnFunctionConfiguration {

        private Boolean enabled = Boolean.TRUE;
    }

    /**
     * 列的属性名称转换为小写函数配置
     */
    @Getter
    @Setter
    public static class FieldNameLowerCaseColumnFunctionConfiguration {

        private Boolean enabled = Boolean.TRUE;
    }

    /**
     * GaussDB 表备注信息获取函数配置
     */
    @Getter
    @Setter
    public static class GaussDBCommentTableFunctionConfiguration {

        private Boolean enabled = Boolean.TRUE;
    }

    /**
     * 表名称转换为小写函数配置
     */
    @Getter
    @Setter
    public static class TableNameLowerCaseTableFunctionConfiguration {

        private Boolean enabled = Boolean.TRUE;
    }
}
