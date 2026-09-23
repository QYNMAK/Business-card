package com.pfh.crm.dashboard.common.model;

import com.pfh.crm.dashboard.common.exception.BusinessException;

/**
 * 响应包装工具类
 */
public final class ResponseUtil {

    private static final boolean FAULT_CODE = false;
    private static final boolean SUCCESS_CODE = true;

    private static final String SUCCESS_MSG = "OK";
    private static final String FAULT_MSG = "服务器内部错误";
    private static final String UNKNOWN_MSG = "服务器内部未知错误";

    private ResponseUtil() {
    }

    /**
     * 业务异常统一以 success=false 返回，message 透传业务信息。
     * 不再用 Boolean.parseBoolean(code) 判断——业务错误码是字符串（"-1"/"100001"等），
     * parseBoolean 永远返回 false 只是碰巧正确，语义上不清晰。
     */
    public static <T> Result<T> getBusinessResult(BusinessException ex) {
        return new Result<T>(FAULT_CODE, ex.getMessageWithoutCode());
    }

    public static <T> Result<T> getErrorResult(String msg) {
        return new Result<T>(FAULT_CODE, msg);
    }

    public static <T> Result<T> getFaultResult() {
        return new Result<T>(FAULT_CODE, FAULT_MSG);
    }

    public static <T> Result<T> getSuccessResult(T obj) {
        return new Result<T>(SUCCESS_CODE, SUCCESS_MSG, obj);
    }

    public static <T> Result<T> getSuccessResult(T obj, String... exculde) {
        return new Result<T>(SUCCESS_CODE, SUCCESS_MSG, obj, exculde);
    }

    public static <T> Result<T> getUnknownResult() {
        return new Result<T>(FAULT_CODE, UNKNOWN_MSG);
    }
}
