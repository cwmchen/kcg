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
package com.kaadog.kcg.core.mapping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.kaadog.kcg.core.enums.TableTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Table implements Serializable {

    private static final long serialVersionUID = -7342856478709410269L;

    private String            catalog          = "";
    private String            schema           = "";

    /** table,view */
    private TableTypeEnum     tableType        = TableTypeEnum.TABLE;
    private String            tableName        = "";
    private String            tableComment     = "";

    /** 生成类名称 */
    private String            className        = "";

    /** 包含的列 */
    private List<Column>      columns          = new ArrayList<>();
    /** 索引 */
    private List<Index>       indexs           = new ArrayList<>(1);
    /** 包含的主键 */
    private List<PrimaryKey>  primaryKeys      = new ArrayList<>(1);
    /** 包含的外键 */
    private List<ForeignKey>  foreignKeys      = new ArrayList<>(1);

    public Table addColumn(Column column) {
        columns.add(column);
        return this;
    }

    public Table addIndex(Index index) {
        indexs.add(index);
        return this;
    }

    public Table addPrimaryKey(PrimaryKey primaryKey) {
        primaryKeys.add(primaryKey);
        return this;
    }

    public Table addForeignKey(ForeignKey foreignKey) {
        foreignKeys.add(foreignKey);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        return this.getTableName().equalsIgnoreCase(((Table) obj).getTableName());
    }

}
