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
package com.kaadog.kcg.dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kaadog.kcg.core.JDBCDataSourceGenerator;
import com.kaadog.kcg.core.utils.FileUtil;
import com.kaadog.kcg.dashboard.config.DashboardConfiguration;
import com.kaadog.kcg.dashboard.config.GeneratorConfiguration;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ZipUtil;

@RestController
public class GeneratorController {

    public static final String     DEFAULT_G  = "/g";
    public static final String     DEFAULT_GD = "/gd";

    @Autowired
    private GeneratorConfiguration generatorConfiguration;

    @Autowired
    private DashboardConfiguration dashboardConfiguration;

    /**
     * 代码生成
     * 
     * @param out 是否输出文件流
     * @return out = true 压缩后的文件流，out = false 生成代码存储地址
     * @throws Throwable
     */
    private ResponseEntity<Object> generateCode(boolean out) throws Throwable {
        JDBCDataSourceGenerator generator = new JDBCDataSourceGenerator(generatorConfiguration);

        String path = generator.generate();

        if (!out) {
            return ResponseEntity.ok("{\"path\":\"" + path + "\"}");
        }

        String zipTmpPath = FileUtil.normalize(FileUtil.getTmpDirPath() + "/" + UUID.fastUUID() + ".zip");
        String fileName = dashboardConfiguration.getFileName();
        FileSystemResource file = new FileSystemResource(ZipUtil.zip(path + "/" + fileName, zipTmpPath));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", fileName + ".zip"));
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.contentLength())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(file.getInputStream()));

    }

    /**
     * 代码生成，输出存储地址
     */
    @GetMapping(DEFAULT_G)
    public ResponseEntity<Object> g() throws Throwable {
        return generateCode(false);
    }

    /**
     * 代码生成，输出文件压缩流
     */
    @GetMapping(DEFAULT_GD)
    public ResponseEntity<Object> gd() throws Throwable {
        return generateCode(true);
    }

}
