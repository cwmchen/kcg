package com.kaadog.kcg.core.exception;

/**
 * 数据源工厂日常
 */
public class DataSourceFactoryException extends SystemRunException {

    private static final long serialVersionUID = 1350066238695611093L;

    public DataSourceFactoryException(){
        super();
    }

    public DataSourceFactoryException(String message, Throwable cause){
        super(message, cause);
    }

    public DataSourceFactoryException(String message){
        super(message);
    }

    public DataSourceFactoryException(Throwable cause){
        super(cause);
    }
}
