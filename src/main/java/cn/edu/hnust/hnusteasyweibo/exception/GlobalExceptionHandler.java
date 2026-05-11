package cn.edu.hnust.hnusteasyweibo.exception;

import cn.edu.hnust.hnusteasyweibo.dto.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 统一处理应用程序中发生的各种异常，并返回格式统一的错误响应
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获并处理BusinessException业务异常，返回统一错误响应
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseDTO<?> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage());
        return ResponseDTO.error(e.getCode(), e.getMessage());
    }

    /**
     * 捕获并处理未预期的系统异常，返回通用系统错误响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseDTO<?> handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return ResponseDTO.error(500, "系统内部错误");
    }
}
