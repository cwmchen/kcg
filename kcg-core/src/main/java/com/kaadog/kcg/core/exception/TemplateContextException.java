package com.kaadog.kcg.core.exception;

/**
 * 模板上下文异常
 */
public class TemplateContextException extends SystemRunException {

    private static final long serialVersionUID = 5004273420445116752L;

    public TemplateContextException(){
        super();
    }

    public TemplateContextException(String message, Throwable cause){
        super(message, cause);
    }

    public TemplateContextException(String message){
        super(message);
    }

    public TemplateContextException(Throwable cause){
        super(cause);
    }
}
