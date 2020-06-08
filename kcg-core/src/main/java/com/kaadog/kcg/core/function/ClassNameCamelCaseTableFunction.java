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
package com.kaadog.kcg.core.function;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import com.kaadog.kcg.core.mapping.Table;

/**
 * 类名使用驼峰命名，并且进行特殊标识字符串进行替换
 */
public class ClassNameCamelCaseTableFunction implements TransformFunction<Connection, List<Table>> {

    public static final List<String> DEFAULT_SEARCH_STRS = Arrays.asList("t_", "_t_", "t-", "-t-", "-");

    /** 搜索的字符串，按照顺序搜索，设定时要注意字符串顺序 */
    private List<String>             searchStrs          = DEFAULT_SEARCH_STRS;
    /** 替换的字符串，方便进行驼峰转换 */
    private final String             replacement         = "_";

    public ClassNameCamelCaseTableFunction(){

    }

    public ClassNameCamelCaseTableFunction(List<String> searchStrs){
        this.searchStrs = searchStrs;
    }

    @Override
    public void accept(Connection c, List<Table> tables) {
        tables.forEach(table -> {
            // 替换字符串
            searchStrs.forEach(str -> {
                String className = table.getClassName();
                className = StrUtil.replace(className, 0, str, replacement, true);
                table.setClassName(className);
            });
            // 驼峰命名
            String className = table.getClassName();
            className = StrUtil.upperFirst(StrUtil.toCamelCase(className));
            table.setClassName(className);
        });
    }

    public void setSearchStrs(List<String> searchStrs) {
        this.searchStrs = searchStrs;
    }
}
