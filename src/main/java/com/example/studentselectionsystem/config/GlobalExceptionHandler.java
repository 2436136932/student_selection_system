package com.example.studentselectionsystem.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/**
 * 全局异常处理类
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理ResponseStatusException，保留原始状态码和错误消息
     * 这个方法会优先处理ResponseStatusException异常
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        return new ResponseEntity<>(ex.getReason(), ex.getStatusCode());
    }

    /**
     * 处理权限不足异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
        return new ResponseEntity<>("权限不足", HttpStatus.FORBIDDEN);
    }

    /**
     * 只处理学号重复异常的特殊运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        String message = ex.getMessage();
        if (message != null && message.equals("学号已存在")) {
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
        // 对于其他运行时异常，让它继续传播，最终会被Spring Boot的默认异常处理器处理
        // 这样可以确保ResponseStatusException等特定异常能被正确处理
        throw ex;
    }
}
