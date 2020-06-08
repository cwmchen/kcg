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
package com.kaadog.kcg.core.configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 方言配置
 */
@Getter
@Setter
public class DialectConfiguration {

    public static final List<TypeMappingConfiguration> DEFAULT_TYPES = new ArrayList<>();

    static {
        DEFAULT_TYPES.add(new TypeMappingConfiguration("varchar", "String"));
        DEFAULT_TYPES.add(new TypeMappingConfiguration("varchar2", "String"));
        DEFAULT_TYPES.add(new TypeMappingConfiguration("double", "double"));
        DEFAULT_TYPES.add(new TypeMappingConfiguration("number", "double"));
        DEFAULT_TYPES.add(new TypeMappingConfiguration("boolean", "Boolean", "Boolean.TRUE"));
        DEFAULT_TYPES.add(new TypeMappingConfiguration("integer", "int"));
        DEFAULT_TYPES.add(new TypeMappingConfiguration("int", "int"));
        DEFAULT_TYPES.add(new TypeMappingConfiguration("clob", "String"));
        DEFAULT_TYPES.add(new TypeMappingConfiguration("char", "String"));
        DEFAULT_TYPES.add(new TypeMappingConfiguration("datetime", "LocalDateTime", "java.time.LocalDateTime", "LocalDateTime.now()"));
        DEFAULT_TYPES.add(new TypeMappingConfiguration("date", "LocalDateTime", "java.time.LocalDateTime", "LocalDateTime.now()"));
        DEFAULT_TYPES.add(new TypeMappingConfiguration("longtext", "String"));
        DEFAULT_TYPES.add(new TypeMappingConfiguration("timestamp", "LocalDateTime", "java.time.LocalDateTime", "LocalDateTime.now()"));
    }

    /** 数据库字段与特定语言类型映射配置 */
    private List<TypeMappingConfiguration> types;

    public TypeMappingConfiguration getTypeMappingConfiguration(String dataType) {

        Optional<TypeMappingConfiguration> optional = types.stream().filter(type -> {
            return type.getDataType().equalsIgnoreCase(dataType);
        }).findFirst();

        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    public void setTypes(List<TypeMappingConfiguration> types) {
        this.types = new ArrayList<>(DEFAULT_TYPES);
        this.types.addAll(types);
    }

    /**
     * 数据库字段与特定语言类型映射配置
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TypeMappingConfiguration implements Serializable {

        private static final long serialVersionUID = 8059972457303767347L;

        /** 数据库类型 */
        private String            dataType         = "";
        /** 字段类型 */
        private String            fieldType        = "";
        /** 字段类型所使用的类名，如果指定会进行 import（非内置类型需要指定） */
        private String            className        = "";
        /** 默认值 */
        private String            value            = "";

        public TypeMappingConfiguration(String dataType, String fieldType){
            this(dataType, fieldType, "", "");
        }

        public TypeMappingConfiguration(String dataType, String fieldType, String value){

            this(dataType, fieldType, "", value);
        }
    }
}
