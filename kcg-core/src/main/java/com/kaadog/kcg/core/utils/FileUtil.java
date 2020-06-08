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
package com.kaadog.kcg.core.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtil extends cn.hutool.core.io.FileUtil {

    /**
     * 获取指定路径下的目录地址，包含子目录地址
     * 
     * @param path 可以是绝对路径，也可以是 classpath 标识的路径
     */
    public static List<String> getDirectoryPaths(String path) {
        List<String> paths = new ArrayList<>();
        String absolutePath = getAbsolutePath(path);
        File[] files = ls(absolutePath);
        Arrays.asList(files).forEach(file -> {
            if (file.isDirectory()) {
                paths.add(getCanonicalPath(file));
                paths.addAll(getDirectoryPaths(file.getPath()));
            }
        });
        return paths.stream().map(p -> {
            return normalize(p);
        }).collect(Collectors.toList());
    }

    /**
     * 替换路径
     * 
     * @param path 原始路径
     * @param primaryStr 原始字符串
     * @param finalStr 替换后的字符串
     */
    public static String replacePath(String path, String primaryStr, String finalStr) {
        return normalize(path.replaceAll(FileUtil.getAbsolutePath(primaryStr), FileUtil.getAbsolutePath(finalStr)));
    }
}
