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
package com.kaadog.kcg.core.mapping;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 索引
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Index implements Serializable {

    private static final long serialVersionUID = 3140889089099746889L;

    private String            catalog          = "";
    private String            schema           = "";
    private String            tableName        = "";

    /** 是否唯一索引 */
    private Boolean           nonUnique        = Boolean.TRUE;
    /** 索引目录 */
    private String            indexQualifier   = "";
    /** 索引名称 */
    private String            indexName        = "";
    /** 索引类型 */
    private int               type;
    /** 顺序 */
    private int               ordinalPosition;
    /** 列名 */
    private String            columnName       = "";
    /** 排序序列 */
    private String            ascOrDesc        = "";
    /** 过滤条件 */
    private String            filterCondition  = "";
}
