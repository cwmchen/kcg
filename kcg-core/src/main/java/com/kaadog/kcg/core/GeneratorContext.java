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
package com.kaadog.kcg.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.ObjectUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class GeneratorContext {

    /** 目录配置数据同时包含文件配置数据 */
    private List<DirectoryConfiguration> directoryConfigurations = new ArrayList<>();

    /** 全局数据，会附加到 dataMaps 中每一个 Map 对象中，如果 Map 中存在不进行覆盖 */
    private Map<String, Object>          globalData              = new HashMap<>();

    private List<Map<String, Object>>    dataMaps                = new ArrayList<>();

    public GeneratorContext addDirectoryConfiguration(DirectoryConfiguration directoryConfiguration) {
        directoryConfigurations.add(directoryConfiguration);
        return this;
    }

    public GeneratorContext putData(Map<String, Object> data) {
        dataMaps.add(data);
        return this;
    }

    /**
     * 附加全局数据到数据集合中
     */
    public List<Map<String, Object>> getAvailableDataMaps() {
        List<Map<String, Object>> newDataMaps = new ArrayList<>(ObjectUtil.cloneByStream(dataMaps));

        newDataMaps.forEach(dataMap -> {
            globalData.forEach((key, value) -> {
                if (!dataMap.containsKey(key)) {
                    dataMap.put(key, value);
                }
            });
        });

        return newDataMaps;
    }

    /**
     * 目录配置数据，根据此数据进行最终的代码生成
     */
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DirectoryConfiguration {

        /** 原始路径 */
        private String                  originalPath;
        /** 最终路径 */
        private String                  finalPath;
        /** 当前目录下的文件 */
        private List<FileConfiguration> fileConfigurations = new ArrayList<>();

        /** 当前目录所需数据 */
        private Map<String, Object>     data;
    }

    /**
     * 文件文件
     */
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileConfiguration {

        /** 文件名称 */
        private String              fileName;
        /** 原始路径 */
        private String              originalPath;
        /** 最终路径 */
        private String              finalPath;

        /** 当前文件所需数据 */
        private Map<String, Object> data;
    }
}
