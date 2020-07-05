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

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.kaadog.kcg.core.GeneratorContext.DirectoryConfiguration;
import com.kaadog.kcg.core.GeneratorContext.FileConfiguration;
import com.kaadog.kcg.core.configuration.TemplateConfiguration;
import com.kaadog.kcg.core.exception.TemplateContextException;
import com.kaadog.kcg.core.utils.FileUtil;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.crypto.SecureUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * 模板上下文
 * <p>
 * 提供一下主要功能：
 * <ol>
 * <li>当未指定输出文件夹时进行默认文件夹创建，默认创建在用户目录中</li>
 * <li>如果是zip、jar 文件时需要解压，默认使用临时文件目录</li>
 * <li>获取目录配置数据 {@link DirectoryConfiguration}</li>
 * </ol>
 */
@Getter
@Setter
public class TemplateContext {

    private TemplateConfiguration templateConfiguration;

    /** 如果是zip、jar 文件时需要解压, 防止文件夹重复生成的临时文件夹名称 */
    private String                zipOrJarTempFolderName;

    /** 最终解压后的临时模板文件夹 */
    private String                finalTempFolderPath;
    /** 最终生成文件输出文件夹 */
    private String                finalOutRootFolder;

    /**
     * 模板名称格式化
     */
    public String templateNameFormat(String path) {
        return SecureUtil.sha256(path);
    }

    /**
     * 如果是zip、jar 文件时需要解压，默认使用临时文件目录
     */
    private String getZipOrJarTemp() {

        String folderPathSha256 = SecureUtil.sha256(templateConfiguration.getFolderPath());
        String tempFolderPath = templateConfiguration.getTempFolderPath();

        FileUtil.mkdir(tempFolderPath);
        File[] files = FileUtil.ls(tempFolderPath);
        // 创建当前模板文件夹的索引号，保障不被别的线程替换调里面的内容
        int index = 0;
        for (File file : files) {
            if (file.isDirectory()) {
                deleteExpiredFolders(file);
            }

            if (file.isDirectory() && file.getName().endsWith(folderPathSha256)) {
                index = index + 1;
            }
        }
        String zipOrJarTempFolderName = index + "_" + folderPathSha256;
        setZipOrJarTempFolderName(zipOrJarTempFolderName);

        String dirPath = FileUtil.normalize(tempFolderPath + "/" + zipOrJarTempFolderName + "/");
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    /**
     * 删除过期的文件夹
     */
    private Boolean deleteExpiredFolders(File file) {
        Date date = FileUtil.lastModifiedTime(file);
        long day = DateUtil.between(date, DateUtil.date(), DateUnit.DAY, true);
        if (day > templateConfiguration.getTempFolderRetainDay()) {
            return FileUtil.del(file);
        }
        return true;
    }

    /**
     * 获取目录配置数据
     * <p>
     * classpath :
     * <ol>
     * <li>读取指定 classpath 下的所有资源目录数据</li>
     * <li>将资源目录数据重新拷贝在临时文件夹</li>
     * </ol>
     * <p>
     * jar、zip :
     * <ol>
     * <li>解压文件内容到临时文件夹</li>
     * </ol>
     * <p>
     * 以上统一处理完成后读取临时文件夹中的目录结构，生成 {@link DirectoryConfiguration} 集合
     */
    public List<DirectoryConfiguration> getDirectoryConfigurations() {
        try {
            String folderPath = templateConfiguration.getFolderPath();
            String finalTempFolderPath = "";

            String fileEncoding = templateConfiguration.getFileEncoding();

            // 解压目录
            String zipOrJarTemp = getZipOrJarTemp();

            // 当模板文件夹是 classpath 时
            if (folderPath.toLowerCase().startsWith(URLUtil.CLASSPATH_URL_PREFIX)) {
                // 获取 classpath 中指定的模板文件夹路径
                String classPathFolderPath = FileUtil
                        .normalize(StrUtil.removePrefixIgnoreCase(folderPath, URLUtil.CLASSPATH_URL_PREFIX));

                // 最终模板存放目录
                finalTempFolderPath = FileUtil.normalize(zipOrJarTemp + classPathFolderPath);

                List<URL> urls = Collections.list(this.getClass().getClassLoader().getResources(classPathFolderPath));

                for (URL url : urls) {
                    String absolutePath = FileUtil.getAbsolutePath(url.getPath());
                    int index = absolutePath.lastIndexOf(FileUtil.JAR_PATH_EXT);
                    if (index != -1) {
                        index = index + FileUtil.JAR_FILE_EXT.length();

                        ZipUtil.unzip(absolutePath.substring(0, index), zipOrJarTemp, Charset.forName(fileEncoding));

                    } else {
                        String destPath = finalTempFolderPath
                                .substring(0, FileUtil.lastIndexOfSeparator(finalTempFolderPath));
                        FileUtil.copy(absolutePath, destPath, true);
                    }
                }
            } else {
                String fileSuffix = FileUtil.JAR_PATH_EXT;
                // 当模板文件是文件时，支持 jar、zip，此操作先解压在获取目录结构，并生成 DirectoryConfiguration 集合
                int index = folderPath.lastIndexOf(FileUtil.JAR_PATH_EXT);

                if (index == -1) {
                    index = folderPath.lastIndexOf(".zip!");
                    fileSuffix = ".zip!";
                }
                if (index == -1) {
                    index = folderPath.lastIndexOf(".gz!");
                    fileSuffix = ".gz!";
                }

                String pathTemplateFolder = "";
                if (index != -1) {
                    index = index + fileSuffix.length();

                    // 获取文件后缀对应的目录路径，如果不指定默认使用所有文件
                    pathTemplateFolder = folderPath.substring(index);
                    // 文件路径，去除 !xx/xx/xx 路径
                    folderPath = folderPath.substring(0, index - 1);
                }

                if (FileUtil.isFile(folderPath)) {
                    finalTempFolderPath = zipOrJarTemp + pathTemplateFolder;

                    String extName = FileUtil.extName(folderPath);
                    if (extName.equalsIgnoreCase("jar") || extName.equalsIgnoreCase("zip")) {
                        ZipUtil.unzip(folderPath, zipOrJarTemp, Charset.forName(fileEncoding));
                    } else if (extName.equalsIgnoreCase("gz")) {
                        // 先进行 gzip 解压为 zip 文件
                        byte[] buf = ZipUtil.unGzip(FileUtil.getInputStream(folderPath));
                        String path = zipOrJarTemp + FileUtil.getName(folderPath) + ".zip";
                        FileUtil.writeBytes(buf, path);
                        // 然后解压 zip 文件
                        ZipUtil.unzip(path, zipOrJarTemp, Charset.forName(fileEncoding));
                        FileUtil.del(path);
                    } else {
                        throw new TemplateContextException("不支持 " + extName + " 文件类型");
                    }
                } else {
                    // 指定为特定文件夹时进行复制到工作空间中
                    pathTemplateFolder = folderPath.substring(folderPath.lastIndexOf("/") + 1);
                    finalTempFolderPath = zipOrJarTemp;

                    FileUtil.copy(new File(FileUtil.getAbsolutePath(folderPath)), FileUtil.file(finalTempFolderPath),
                                  true);
                    // 复制目录时完整复制，需要更改最终地址
                    finalTempFolderPath = finalTempFolderPath + pathTemplateFolder;
                }
            }

            // 设置最终模板存放的文件夹
            List<DirectoryConfiguration> list = new ArrayList<>();
            List<String> directoryPaths = FileUtil.getDirectoryPaths(finalTempFolderPath);
            // 当不存在子目录时设置当前目录为模板存放目录
            if (CollectionUtils.isEmpty(directoryPaths)) {
                directoryPaths.add(finalTempFolderPath);
            }

            directoryPaths.forEach(directoryPath -> {

                DirectoryConfiguration directoryConfiguration = new DirectoryConfiguration();
                directoryConfiguration.setOriginalPath(directoryPath);

                List<String> fileNames = FileUtil.listFileNames(directoryPath);

                List<FileConfiguration> fileConfigurations = new ArrayList<>();

                fileNames.forEach(fileName -> {
                    String filePath = FileUtil.normalize(directoryPath + "/" + fileName);

                    FileConfiguration fileConfiguration = new FileConfiguration();
                    fileConfiguration.setFileName(fileName);
                    fileConfiguration.setOriginalPath(filePath);
                    fileConfigurations.add(fileConfiguration);
                });

                directoryConfiguration.setFileConfigurations(fileConfigurations);
                list.add(directoryConfiguration);
            });
            return list;
        } catch (Exception e) {
            throw new TemplateContextException(e);
        }
    }
}
