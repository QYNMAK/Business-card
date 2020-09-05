package com.pfh.crm.dashboard.common.model;


import com.pfh.crm.dashboard.common.exception.BusinessException;

/**
 * 响应包装
 *
 * @author pengfh
 * @date 2019/10/8
 */
public final class ResponseUtil {

    private static final boolean FAULT_CODE = false;
    private static final boolean SUCCESS_CODE = true;
    private static final int N0_PERMS_CODE = 403;

    private static final String SUCCESS_MSG = "OK";
    private static final String FAULT_MSG = "服务器内部错误";
    private static final String UNKNOWN_MSG = "服务器内部未知错误";
    private static final String N0_PERMS = "没有相关操作权限";


    public static <T> Result<T> getBusinessResult(BusinessException ex) {
        return new Result<T>(Boolean.parseBoolean(ex.getCode()), ex.getMessageWithoutCode());
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