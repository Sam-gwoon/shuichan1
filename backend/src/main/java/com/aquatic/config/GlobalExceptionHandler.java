package com.aquatic.config;

import com.aquatic.dto.ApiResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ApiResult<?> handleRuntimeException(RuntimeException e) {
        return ApiResult.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResult<?> handleException(Exception e) {
        return ApiResult.error("服务器内部错误: " + e.getMessage());
    }
}
