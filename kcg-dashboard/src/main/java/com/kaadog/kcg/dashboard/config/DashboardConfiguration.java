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
package com.kaadog.kcg.dashboard.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties("kaadog.kcg.dashboard")
public class DashboardConfiguration {

    public static final String     DEFAULT_API_SERVER_PATH = "http://127.0.0.1:1666";
    public static final String     DEFAULT_FILE_NAME       = "fileName";

    /** 下载时生成的文件名称, 不需要后缀 */
    private String                 fileName                = DEFAULT_FILE_NAME;
    /** 接口服务地址 */
    private String                 apiServerPath           = DEFAULT_API_SERVER_PATH;

    /** 跨域注册器 */
    private List<CorsRegistration> corsRegistrations;

    public String getFileName() {
        if (ObjUtil.isEmpty(fileName)) {
            return DEFAULT_FILE_NAME;
        }
        return fileName;
    }

    public static class CorsRegistration {

        public static final String            ALL                            = "*";

        private static final List<HttpMethod> DEFAULT_METHODS                = Arrays
                .asList(HttpMethod.GET, HttpMethod.HEAD, HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH,
                        HttpMethod.DELETE, HttpMethod.OPTIONS, HttpMethod.TRACE);

        private static final List<String>     DEFAULT_PERMIT_METHODS         = Arrays
                .asList(HttpMethod.GET.name(), HttpMethod.HEAD.name(), HttpMethod.POST.name());

        private static final List<String>     DEFAULT_PERMIT_ALL             = Arrays.asList(ALL);

        private static final List<String>     DEFAULT_PERMIT_EXPOSED_HEADERS = Arrays
                .asList(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);

        /** 请求地址, 支持 SpEL */
        private String                        pathPattern                    = "/**";

        /** 哪些域名可以被允许 */
        private List<String>                  allowedOriginPatterns          = DEFAULT_PERMIT_ALL;

        /** 请求的哪些方法可以被允许 */
        private List<String>                  allowedMethods                 = DEFAULT_PERMIT_METHODS;

        /** 可以参与跨域访问的请求方法, 一般比 allowedMethods 多 */
        private List<HttpMethod>              resolvedMethods                = DEFAULT_METHODS;

        /** 表示跨域请求的头部的允许范围 */
        private List<String>                  allowedHeaders                 = DEFAULT_PERMIT_ALL;

        /** 表示暴露哪些头部信息，并提供给客户端 */
        private List<String>                  exposedHeaders                 = DEFAULT_PERMIT_EXPOSED_HEADERS;

        /** 表示是否允许客户端获取用户凭据 */
        private Boolean                       allowCredentials               = true;

        /** 表示预检请求的最大缓存时间 */
        private Long                          maxAge                         = 1800L;

        public String getPathPattern() {
            return pathPattern;
        }

        public void setPathPattern(String pathPattern) {
            this.pathPattern = pathPattern;
        }

        public String[] getAllowedOriginPatterns() {
            return allowedOriginPatterns.stream().toArray(String[]::new);
        }

        public void setAllowedOriginPatterns(List<String> allowedOriginPatterns) {
            this.allowedOriginPatterns = allowedOriginPatterns;
        }

        public String[] getAllowedMethods() {
            return allowedMethods.stream().toArray(String[]::new);
        }

        public void setAllowedMethods(List<String> allowedMethods) {
            this.allowedMethods = allowedMethods;
        }

        public HttpMethod[] getResolvedMethods() {
            return resolvedMethods.stream().toArray(HttpMethod[]::new);
        }

        public void setResolvedMethods(List<HttpMethod> resolvedMethods) {
            this.resolvedMethods = resolvedMethods;
        }

        public String[] getAllowedHeaders() {
            return allowedHeaders.stream().toArray(String[]::new);
        }

        public void setAllowedHeaders(List<String> allowedHeaders) {
            this.allowedHeaders = allowedHeaders;
        }

        public String[] getExposedHeaders() {
            if (exposedHeaders == null) {
                return null;
            }

            return exposedHeaders.stream().toArray(String[]::new);
        }

        public void setExposedHeaders(List<String> exposedHeaders) {
            this.exposedHeaders = exposedHeaders;
        }

        public Boolean getAllowCredentials() {
            return allowCredentials;
        }

        public void setAllowCredentials(Boolean allowCredentials) {
            this.allowCredentials = allowCredentials;
        }

        public Long getMaxAge() {
            return maxAge;
        }

        public void setMaxAge(Long maxAge) {
            this.maxAge = maxAge;
        }
    }
}
