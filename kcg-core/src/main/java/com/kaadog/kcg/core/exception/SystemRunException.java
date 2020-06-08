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
package com.kaadog.kcg.core.exception;

/**
 * 系统运行异常基类，所有异常继承与它
 */
public class SystemRunException extends RuntimeException {

    private static final long  serialVersionUID          = -8685758872933271907L;

    public static final String DEFAULT_MESSAGE           = "失败";
    public static final String DEFAULT_ERROR_STATUS_CODE = "500";

    /** 错误码 */
    private String             errorCode;

    /** 错误状态码 */
    private String             errorStatusCode           = DEFAULT_ERROR_STATUS_CODE;

    public SystemRunException(){
        super(DEFAULT_MESSAGE);
    }

    public SystemRunException(String message, Throwable cause){
        super(message, cause);
    }

    public SystemRunException(String message){
        super(message);
    }

    public SystemRunException(Throwable cause){
        super(cause);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorStatusCode() {
        return errorStatusCode;
    }

    public void setErrorStatusCode(String errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }
}
