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
import java.util.List;

import com.kaadog.kcg.core.mapping.Column;

/**
 * 数据类型转换为小写
 */
public class DataTypeLowerCaseColumnFunction implements TransformFunction<Connection, List<Column>>{

    @Override
    public void accept(Connection c, List<Column> columns) {
        columns.forEach(column -> {
            String dataType = column.getDataType().toLowerCase();
            column.setDataType(dataType);
        });
    }
}
