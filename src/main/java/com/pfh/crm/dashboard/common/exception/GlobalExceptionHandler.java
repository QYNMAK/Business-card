package com.pfh.crm.dashboard.common.exception;

import com.alibaba.fastjson.JSONException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.pfh.crm.dashboard.common.model.ResponseUtil;
import com.pfh.crm.dashboard.common.model.Result;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
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
 * 异常统一处理
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ResponseBody
    @ExceptionHandler(value = InvalidFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> fastJsonInvalidFormatException(InvalidFormatException e) {
        return ResponseUtil.getErrorResult(e.getOriginalMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = JSONException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> jsonException(JSONException e) {
        return ResponseUtil.getErrorResult("输入数据格式有误！");
    }

    @ResponseBody
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<ObjectError> errors = ex.getBindingResult().getAllErrors();
        LinkedList<String> messages = new LinkedList<>();
        Iterator<ObjectError> iterator = errors.iterator();
        while (iterator.hasNext()) {
            FieldError error = (FieldError) iterator.next();
            messages.add(error.getDefaultMessage());
        }
        return messages.isEmpty()
                ? ResponseUtil.getErrorResult("参数校验失败")
                : ResponseUtil.getErrorResult(messages.get(0));
    }

    @ResponseBody
    @ExceptionHandler(value = {BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> bindException(BindException ex) {
        List<ObjectError> errors = ex.getAllErrors();
        LinkedList<String> messages = new LinkedList<>();
        for (ObjectError error : errors) {
            FieldError fieldError = (FieldError) error;
            messages.add(MessageFormat.format("{0}类型不正确", fieldError.getField()));
        }
        String message = StringUtils.collectionToDelimitedString(messages, ",");
        return ResponseUtil.getErrorResult(message);
    }

    @ResponseBody
    @ExceptionHandler(value = {ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> constraintViolationException(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        LinkedList<String> messages = new LinkedList<>();
        for (ConstraintViolation<?> cv : violations) {
            messages.add(cv.getMessage());
        }
        String message = StringUtils.collectionToDelimitedString(messages, ",");
        return ResponseUtil.getErrorResult(message);
    }

    @ResponseBody
    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        return ResponseUtil.getErrorResult(String.format("参数:%s类型不正确", ex.getName()));
    }

    /**
     * 业务异常：BusinessException 的 code 是业务错误码字符串（如 "-1" / "100001"），
     * 一律以 success=false 响应，message 直接透传。
     */
    @ResponseBody
    @ExceptionHandler(value = {BusinessException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> businessException(BusinessException e) {
        return ResponseUtil.getBusinessResult(e);
    }

    @ExceptionHandler(value = FileUploadBase.FileSizeLimitExceededException.class)
    @ResponseBody
    public Result exceptionFileSizeLimitExceededException(FileUploadBase.FileSizeLimitExceededException e) {
        LOGGER.error("文件大小超限: {}", e.getMessage());
        return ResponseUtil.getErrorResult("单个文件不能大于10MB");
    }

    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    @ResponseBody
    public Result exceptionMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        LOGGER.error("请求大小超限: {}", e.getMessage());
        return ResponseUtil.getErrorResult("单个文件不能大于10MB");
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result exceptionGet(Exception e) {
        LOGGER.error("【系统异常】", e);
        return ResponseUtil.getErrorResult("服务器忙");
    }
}
