package com.kaadog.kcg.core.exception;

/**
 * Freemarker 配置异常
 */
public class FreemarkerConfigurationException extends SystemRunException {

    private static final long serialVersionUID = -7945743611813594462L;

    public FreemarkerConfigurationException(){
        super();
    }

    public FreemarkerConfigurationException(String message, Throwable cause){
        super(message, cause);
    }

    public FreemarkerConfigurationException(String message){
        super(message);
    }

    public FreemarkerConfigurationException(Throwable cause){
        super(cause);
    }
}
