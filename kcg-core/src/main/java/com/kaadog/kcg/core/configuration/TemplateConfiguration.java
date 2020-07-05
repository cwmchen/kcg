package com.kaadog.kcg.core.configuration;

import org.apache.commons.lang3.StringUtils;

import com.kaadog.kcg.core.utils.FileUtil;

import lombok.Getter;
import lombok.Setter;

/**
 * 模板配置
 */
@Getter
@Setter
public class TemplateConfiguration {

    public static final String DEFAULT_TEMPLATE_FOLDER      = "../conf/template";
    public static final String DEFAULT_OUT_FOLDER           = "../out";
    public static final String DEFAULT_WOKR_FOLDER          = "/kcg/";
    public static final String DEFAULT_TEMP_TEMPLATE_FOLDER = DEFAULT_WOKR_FOLDER + "template";

    /**
     * 模板存放的，
     * <p>
     * 路径支持：
     * <ol>
     * <li></li>
     * <li>本地磁盘地址</li>
     * <li>相对程序所在文件夹的相对路径</li>
     * <li>jar 文件路径</li>
     * <li>压缩文件路径（rar、tar.gz）</li>
     * <li>classpath 路径</li>
     * </ol>
     * 可以使用绝对路径、相对路径 ，默认值：{@link TemplateConfiguration#DEFAULT_TEMPLATE_FOLDER}
     */
    private String             folderPath                   = DEFAULT_TEMPLATE_FOLDER;

    /** 模板后缀，非此后缀不会进行模板读取，直接复制文件到输出文件夹，默认使用 .flt */
    private String             fileSuffix                   = ".flt";
    /** 模板文件编码，默认使用 UTF-8 */
    private String             fileEncoding                 = "UTF-8";

    /** 生成文件输出文件夹（文件夹必须存在），默认值：{@link TemplateConfiguration#DEFAULT_OUT_FOLDER} */
    private String             outRootFolder                = DEFAULT_OUT_FOLDER;
    /** 如果是 jar、zip 时此路径会执行解压路径（文件夹必须存在），默认值：用户临时文件夹+{@link TemplateConfiguration#DEFAULT_TEMP_TEMPLATE_FOLDER} */
    private String             tempFolderPath               = "";
    /** 解压后的临时模板文件夹保留的天数 */
    private int                tempFolderRetainDay          = 1;

    public String getFolderPath() {
        return folderPath;
    }

    public String getOutRootFolder() {
        return FileUtil.normalize(FileUtil.getAbsolutePath(outRootFolder));
    }

    public String getTempFolderPath() {
        if (StringUtils.isBlank(tempFolderPath)) {
            return FileUtil.normalize(FileUtil.getAbsolutePath(FileUtil.getTmpDir()) + DEFAULT_TEMP_TEMPLATE_FOLDER);
        }
        return FileUtil.normalize(FileUtil.getAbsolutePath(tempFolderPath));
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }
}
