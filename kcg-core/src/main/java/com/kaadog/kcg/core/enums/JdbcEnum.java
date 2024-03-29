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
package com.kaadog.kcg.core.enums;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import cn.hutool.core.util.ObjUtil;

public enum JdbcEnum {

                      MYSQL("jdbc:mysql", "com.mysql.cj.jdbc.Driver"),
                      ORACLE("jdbc:oracle", "oracle.jdbc.OracleDriver"),
                      SQLSERVER("jdbc:microsoft", "com.microsoft.sqlserver.jdbc.SQLServerDriver"),
                      GAUSS100("jdbc:zenith", "com.huawei.gauss.jdbc.ZenithDriver"),
                      DB2("jdbc:db2", "com.ibm.db2.jcc.DB2Driver"),
                      POSTGRESQL("jdbc:postgresql", "org.postgresql.Driver"),
                      SYBASE("jdbc:sybase", "com.sybase.jdbc.SybDriver"),
                      INFORMIX("jdbc:informix-sqli", "com.informix.jdbc.IfxDriver");

    private String                             prefix;
    private String                             driverClassName;

    private static final Map<String, JdbcEnum> mappings = new HashMap<>(2);

    static {
        for (JdbcEnum jdbcEnum : values()) {
            mappings.put(jdbcEnum.getPrefix(), jdbcEnum);
        }
    }

    private JdbcEnum(String prefix, String driverClassName) {
        this.prefix = prefix;
        this.driverClassName = driverClassName;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public static JdbcEnum getJdbcEnum(String url) {
        if (ObjUtil.isEmpty(url)) {
            throw new IllegalArgumentException("url must not be null");
        }
        Optional<Entry<String, JdbcEnum>> optional = mappings.entrySet().stream().filter(o -> {
            return url.toLowerCase().startsWith(o.getKey());
        }).findFirst();
        return optional.isPresent() ? optional.get().getValue() : null;
    }

}
