package com.nusiss.shoppingcart.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 全局异常处理器
 * 统一处理应用中的各种异常，提供友好的错误响应
 * @author SpringCA Team
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessException(
            BusinessException e, HttpServletRequest request) {
        
        log.warn("业务异常: {} - {}", e.getCode(), e.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("code", e.getCode());
        response.put("message", e.getMessage());
        response.put("timestamp", System.currentTimeMillis());
        response.put("path", request.getRequestURI());
        
        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }
    
    /**
     * 处理参数验证异常 - @Valid注解
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        
        log.warn("参数验证失败: {}", e.getMessage());
        
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("code", "VALIDATION_ERROR");
        response.put("message", "参数验证失败");
        response.put("errors", fieldErrors);
        response.put("timestamp", System.currentTimeMillis());
        response.put("path", request.getRequestURI());
        
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, Object>> handleBindException(
            BindException e, HttpServletRequest request) {
        
        log.warn("数据绑定失败: {}", e.getMessage());
        
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("code", "BIND_ERROR");
        response.put("message", "数据绑定失败");
        response.put("errors", fieldErrors);
        response.put("timestamp", System.currentTimeMillis());
        response.put("path", request.getRequestURI());
        
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * 处理约束验证异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(
            ConstraintViolationException e, HttpServletRequest request) {
        
        log.warn("约束验证失败: {}", e.getMessage());
        
        Map<String, String> violations = new HashMap<>();
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        for (ConstraintViolation<?> violation : constraintViolations) {
            String propertyPath = violation.getPropertyPath().toString();
            violations.put(propertyPath, violation.getMessage());
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("code", "CONSTRAINT_VIOLATION");
        response.put("message", "约束验证失败");
        response.put("violations", violations);
        response.put("timestamp", System.currentTimeMillis());
        response.put("path", request.getRequestURI());
        
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatchException(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        
        log.warn("参数类型不匹配: {} - {}", e.getName(), e.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("code", "TYPE_MISMATCH");
        response.put("message", String.format("参数 '%s' 类型不正确", e.getName()));
        response.put("parameter", e.getName());
        response.put("expectedType", e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown");
        response.put("timestamp", System.currentTimeMillis());
        response.put("path", request.getRequestURI());
        
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * 处理404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFoundException(
            NoHandlerFoundException e, HttpServletRequest request) {
        
        log.warn("请求路径不存在: {} {}", e.getHttpMethod(), e.getRequestURL());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("code", "NOT_FOUND");
        response.put("message", "请求的资源不存在");
        response.put("method", e.getHttpMethod());
        response.put("url", e.getRequestURL());
        response.put("timestamp", System.currentTimeMillis());
        response.put("path", request.getRequestURI());
        
        return ResponseEntity.notFound().build();
    }
    
    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException e, HttpServletRequest request) {
        
        log.warn("非法参数异常: {}", e.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("code", "ILLEGAL_ARGUMENT");
        response.put("message", "参数不合法: " + e.getMessage());
        response.put("timestamp", System.currentTimeMillis());
        response.put("path", request.getRequestURI());
        
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String, Object>> handleNullPointerException(
            NullPointerException e, HttpServletRequest request) {
        
        log.error("空指针异常: ", e);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("code", "NULL_POINTER");
        response.put("message", "系统内部错误，请稍后重试");
        response.put("timestamp", System.currentTimeMillis());
        response.put("path", request.getRequestURI());
        
        return ResponseEntity.internalServerError().body(response);
    }
    
    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException e, HttpServletRequest request) {
        
        log.error("运行时异常: ", e);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("code", "RUNTIME_ERROR");
        response.put("message", "系统运行时错误: " + e.getMessage());
        response.put("timestamp", System.currentTimeMillis());
        response.put("path", request.getRequestURI());
        
        return ResponseEntity.internalServerError().body(response);
    }
    
    /**
     * 处理所有其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception e, HttpServletRequest request) {
        
        log.error("未处理的异常: ", e);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("code", "INTERNAL_ERROR");
        response.put("message", "系统内部错误，请联系管理员");
        response.put("timestamp", System.currentTimeMillis());
        response.put("path", request.getRequestURI());
        
        return ResponseEntity.internalServerError().body(response);
    }
}