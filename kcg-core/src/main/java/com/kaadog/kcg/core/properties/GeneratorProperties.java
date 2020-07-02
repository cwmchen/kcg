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
package com.kaadog.kcg.core.properties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kaadog.kcg.core.configuration.DataSourceConfiguration;
import com.kaadog.kcg.core.configuration.DialectConfiguration;
import com.kaadog.kcg.core.configuration.TemplateConfiguration;
import com.kaadog.kcg.core.configuration.TransformFunctionConfiguration;

import lombok.Getter;
import lombok.Setter;

/**
 * 生成器配置
 */
@Getter
@Setter
public class GeneratorProperties {

    /** 模板配置 */
    private TemplateConfiguration          template;

    /** 自定义属性内容全局可使用 */
    private Map<String, String>            properties        = new HashMap<>();

    /** 数据源配置, 可以指定多个数据源 */
    private List<DataSourceConfiguration>  dataSources;

    /** 数据转换接口配置 */
    private TransformFunctionConfiguration transformFunction = new TransformFunctionConfiguration();

    /** 方言配置 */
    private DialectConfiguration           dialect           = new DialectConfiguration();

}
