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
package com.kaadog.kcg.dashboard.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties("kaadog.kcg.dashboard")
public class DashboardConfiguration {

    public static final String DEFAULT_API_SERVER_PATH = "http://127.0.0.1:1666";
    public static final String DEFAULT_FILE_NAME       = "fileName";

    /** 下载时生成的文件名称, 不需要后缀 */
    private String             fileName                = DEFAULT_FILE_NAME;
    /** 接口服务地址 */
    private String             apiServerPath           = DEFAULT_API_SERVER_PATH;

    public String getFileName() {
        if (StringUtils.isBlank(fileName)) {
            return DEFAULT_FILE_NAME;
        }
        return fileName;
    }
}
