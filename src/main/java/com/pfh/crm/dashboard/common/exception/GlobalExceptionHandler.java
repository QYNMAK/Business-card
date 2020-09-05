package com.pfh.crm.dashboard.common.exception;

import com.alibaba.fastjson.JSONException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.pfh.crm.dashboard.common.model.ResponseUtil;
import com.pfh.crm.dashboard.common.model.Result;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 异常统一处理。
 *
 * @author pengfh
 * @date 2019/10/8
 */
@ControllerAdvice
public class GlobalExceptionHandler {


    private final static Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 映射实体类，格式化异常处理
     *
     * @param e json格式转换异常
     * @return 异常包装信息
     */
    @ResponseBody
    @ExceptionHandler(value = InvalidFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> fastJsonInvalidFormatException(InvalidFormatException e) {
        return ResponseUtil.getErrorResult(e.getOriginalMessage());
    }

    /**
     * 映射实体类，JSON转换异常处理
     *
     * @param e JSON转换异常处理
     * @return 异常包装信息
     */
    @ResponseBody
    @ExceptionHandler(value = JSONException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> jsonException(JSONException e) {
        return ResponseUtil.getErrorResult("输入数据格式有误！");
    }

    /**
     * 参数验证异常处理
     *
     * @param ex 参数无效异常
     * @return 异常包装信息
     */
    @ResponseBody
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<ObjectError> bytes = ex.getBindingResult().getAllErrors();
        LinkedList<String> defaultMessage = new LinkedList<>();
        Iterator var9 = bytes.iterator();
        while (var9.hasNext()) {
            FieldError error = (FieldError) var9.next();
            defaultMessage.add(error.getDefaultMessage());
        }
        return ResponseUtil.getErrorResult(defaultMessage.get(0).toString());
    }

    /**
     * SpringMVC GET方式参数类型转换异常处理
     *
     * @param ex 参数绑定异常
     * @return 异常包装信息
     */
    @ResponseBody
    @ExceptionHandler(value = {BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> bindException(BindException ex) {
        List<ObjectError> errors = ex.getAllErrors();
        Iterator var9 = errors.iterator();
        LinkedList<String> defaultMessage = new LinkedList<String>();
        while (var9.hasNext()) {
            FieldError error = (FieldError) var9.next();
            defaultMessage.add(MessageFormat.format("{0}类型不正确", error.getField()));
        }
        String message = StringUtils.collectionToDelimitedString(defaultMessage, ",");
        return ResponseUtil.getErrorResult(message);
    }

    /**
     * 参数验证错误处理
     *
     * @param ex 异常
     * @return Result<String>
     */
    @ResponseBody
    @ExceptionHandler(value = {ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> constraintViolationException(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> cvs = ex.getConstraintViolations();
        LinkedList<String> defaultMessage = new LinkedList<>();
        for (Object cv : cvs) {
            ConstraintViolationImpl x = (ConstraintViolationImpl) cv;
            defaultMessage.add(x.getMessage());
        }
        String message = StringUtils.collectionToDelimitedString(defaultMessage, ",");
        return ResponseUtil.getErrorResult(message);
    }

    /**
     * SpringMVC POST方式参数类型转换错误处理
     *
     * @param ex 异常
     * @return Result<String>
     */
    @ResponseBody
    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        return ResponseUtil.getErrorResult(String.format("参数:%s类型不正确",ex.getName()));
    }


    /**
     * 系统业务异常
     *
     * @param e 异常
     * @return Result<String>
     */
    @ResponseBody
    @ExceptionHandler(value = {BusinessException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> businessException(BusinessException e) {
        return new Result<>(Boolean.parseBoolean(e.getCode()), e.getMessage());
    }


    /**
     * 文件上传大小限制
     * <p>
     * 暂时关闭
     *
     * @param e 统一处理文件上传大小限制异常。
     * @return Result
     */

    @ExceptionHandler(value = FileUploadBase.FileSizeLimitExceededException.class)
    @ResponseBody
    public Result exceptionFileSizeLimitExceededException(FileUploadBase.FileSizeLimitExceededException e) {
        e.printStackTrace();
        return ResponseUtil.getErrorResult("单个文件不能大于10MB");
    }


    /**
     * 文件上传大小限制
     * <p>
     * 暂时关闭
     *
     * @param e 统一处理文件上传大小限制异常。
     * @return Result
     */

    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    @ResponseBody
    public Result exceptionMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        e.printStackTrace();
        return ResponseUtil.getErrorResult("单个文件不能大于10MB");
    }

    /**
     * 统一处理不可预见的异常。
     * <p>
     * 暂时关闭
     *
     * @param e 统一处理不可预见的异常。
     * @return Result
     */

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result exceptionGet(Exception e) {
        e.printStackTrace();
        LOGGER.error("【系统异常】{}", "异常位置：" + e.getStackTrace()[0] + " 异常原因 " + e.fillInStackTrace());
        return ResponseUtil.getErrorResult("服务器忙");
    }
}
