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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kaadog.kcg.core.enums.TableTypeEnum;

import lombok.Getter;
import lombok.Setter;

/**
 * 数据源配置
 */
@Getter
@Setter
public class DataSourceConfiguration implements Serializable {

    private static final long   serialVersionUID = -148865085058753670L;

    private String              url              = "";
    private String              username         = "";
    private String              password         = "";
    private String              driverClassName  = "";

    private String              catalog          = "";
    private String              schema           = "";

    /** 自定义属性内容 */
    private Map<String, String> properties       = new HashMap<>();

    private List<TableTypeEnum> types            = Arrays.asList(TableTypeEnum.values());

    /** 可以是表名也可以是视图名称，这些表或视图来自数据源配置，按照顺序加载，后加载的会覆盖之前加载的，如果不指定会默认生成所有 */
    private List<String>        tableNames       = new ArrayList<>(1);
}
