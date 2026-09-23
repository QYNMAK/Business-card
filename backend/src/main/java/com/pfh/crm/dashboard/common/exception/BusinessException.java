package com.pfh.crm.dashboard.common.exception;


/**
 * 业务异常
 *
 * @author pengfh
 * @date 2019/10/8
 */
public class BusinessException extends RuntimeException {



    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_FAULT_CODE = "-1";

    private String code;
    private String message;

    public BusinessException(String message) {
        this(DEFAULT_FAULT_CODE, message);

    }


    public BusinessException(String xCode, String message) {
        this(xCode, message, null);
    }

    public BusinessException(String code, String message, Throwable throwable) {
        super("[" + code + "] - " + message, throwable);
        this.message = message;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessageWithoutCode() {
        return message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}