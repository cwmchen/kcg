package com.kaadog.kcg.core.configuration;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 模板配置
 */
@Getter
@Setter
public class TemplateConfiguration {

    private static final String DEFAULT_TEMPLATE_FOLDER     = "../conf/template";
    private static final String DEFAULT_WOKR_FOLDER         = "/kcg/";

    /**
     * 模板存放的，
     * <p>
     * 路径支持：
     * <ol>
     * <li></li>
     * <li>本地磁盘地址</li>
     * <li>相对程序所在目录的相对路径</li>
     * <li>jar 文件路径</li>
     * <li>压缩文件路径（rar、tar.gz）</li>
     * <li>classpath 路径</li>
     * </ol>
     * 可以使用绝对路径、相对路径 ，默认值：{@link DEFAULT_TEMPLATE_FOLDER}
     */
    private String              templateFolder              = DEFAULT_TEMPLATE_FOLDER;

    /** 生成文件输出目录,默认值：用户目录 */
    private String              outRootFolder;
    /** 模板后缀，默认使用 .flt */
    private String              templateFileSuffix          = ".flt";
    /** 模板文件编码，默认使用 UTF-8 */
    private String              templateFileEncoding        = "UTF-8";
    /** 进行模板相关操作的工作文件夹 */
    private String              wokrFolder                  = DEFAULT_WOKR_FOLDER;
    /** 解压后的临时模板文件夹保留的天数 */
    private int                 tempTemplateFolderRetainDay = 1;

    /** 如果是 jar、zip 时此路径会执行解压路径 */
    private String              finalTemplateFolder         = templateFolder;

    public String getTemplateFolder() {
        if (StringUtils.isBlank(templateFolder)) {
            return DEFAULT_TEMPLATE_FOLDER;
        }
        return templateFolder;
    }

    @DeprecatedConfigurationProperty
    public String getFinalTemplateFolder() {
        return finalTemplateFolder;
    }

    public void setTemplateFolder(String templateFolder) {
        this.templateFolder = templateFolder;
        setFinalTemplateFolder(this.templateFolder);
    }
}
