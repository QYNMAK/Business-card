package com.pfh.crm.dashboard.common.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

import java.util.Set;

/**
 * 响应包装
 *
 * @author pengfh
 * @date 2019/10/8
 */
public class Result<T> {
    private boolean success = true;
    private String message;
    private T data;
    @JSONField(serialize = false)
    private String[] exculdes;

    public Result() {
    }


    /**
     * 返回成功数据
     */
    public static  <T> Result<T> ok(T data) {
        return ResponseUtil.getSuccessResult(data);
    }


    /**
     * 返回成功数据
     */
    public static <T> Result<T> ok(T data, String... exculde) {
        return ResponseUtil.getSuccessResult(data, exculde);
    }

    /**
     * 返回操作成功
     */
    public static Result<Object> ok() {
        return ResponseUtil.getSuccessResult(null);
    }


    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Result(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public Result(boolean success, String message, T data, String... exculde) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.exculdes = exculde;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
        Set<String> excludes = filter.getExcludes();
        if (this.exculdes != null) {
            System.err.println("有排除项");
            for (String string : this.exculdes) {
                System.err.println(string);
                excludes.add(string);
            }
        } else {
            System.err.println("没有排除项");
        }

        return JSON.toJSONString(this, filter);
    }

}