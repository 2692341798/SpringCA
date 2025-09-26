package com.nusiss.shoppingcart.exception;

import org.springframework.http.HttpStatus;

/**
 * 业务异常类
 * 用于处理业务逻辑中的异常情况
 * @author SpringCA Team
 */
public class BusinessException extends RuntimeException {
    
    private final String code;
    private final HttpStatus httpStatus;
    
    /**
     * 构造函数
     * @param code 错误代码
     * @param message 错误消息
     */
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }
    
    /**
     * 构造函数
     * @param code 错误代码
     * @param message 错误消息
     * @param httpStatus HTTP状态码
     */
    public BusinessException(String code, String message, HttpStatus httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }
    
    /**
     * 构造函数
     * @param code 错误代码
     * @param message 错误消息
     * @param cause 原因异常
     */
    public BusinessException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }
    
    /**
     * 构造函数
     * @param code 错误代码
     * @param message 错误消息
     * @param cause 原因异常
     * @param httpStatus HTTP状态码
     */
    public BusinessException(String code, String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.code = code;
        this.httpStatus = httpStatus;
    }
    
    public String getCode() {
        return code;
    }
    
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
    
    // 常用的业务异常静态方法
    
    /**
     * 商品不存在异常
     */
    public static BusinessException productNotFound(Long productId) {
        return new BusinessException("PRODUCT_NOT_FOUND", 
            "商品不存在，ID: " + productId, HttpStatus.NOT_FOUND);
    }
    
    /**
     * 库存不足异常
     */
    public static BusinessException insufficientStock(String productName, int requested, int available) {
        return new BusinessException("INSUFFICIENT_STOCK", 
            String.format("商品 '%s' 库存不足，请求数量: %d，可用库存: %d", productName, requested, available));
    }
    
    /**
     * 用户未登录异常
     */
    public static BusinessException userNotLoggedIn() {
        return new BusinessException("USER_NOT_LOGGED_IN", 
            "用户未登录，请先登录", HttpStatus.UNAUTHORIZED);
    }
    
    /**
     * 购物车为空异常
     */
    public static BusinessException cartEmpty() {
        return new BusinessException("CART_EMPTY", "购物车为空");
    }
    
    /**
     * 订单不存在异常
     */
    public static BusinessException orderNotFound(String orderNumber) {
        return new BusinessException("ORDER_NOT_FOUND", 
            "订单不存在，订单号: " + orderNumber, HttpStatus.NOT_FOUND);
    }
    
    /**
     * 订单状态不允许操作异常
     */
    public static BusinessException orderStatusNotAllowed(String orderNumber, String currentStatus, String operation) {
        return new BusinessException("ORDER_STATUS_NOT_ALLOWED", 
            String.format("订单 %s 当前状态为 %s，不允许执行 %s 操作", orderNumber, currentStatus, operation));
    }
    
    /**
     * 用户名已存在异常
     */
    public static BusinessException usernameExists(String username) {
        return new BusinessException("USERNAME_EXISTS", 
            "用户名已存在: " + username, HttpStatus.CONFLICT);
    }
    
    /**
     * 邮箱已存在异常
     */
    public static BusinessException emailExists(String email) {
        return new BusinessException("EMAIL_EXISTS", 
            "邮箱已存在: " + email, HttpStatus.CONFLICT);
    }
    
    /**
     * 登录失败异常
     */
    public static BusinessException loginFailed() {
        return new BusinessException("LOGIN_FAILED", 
            "用户名或密码错误", HttpStatus.UNAUTHORIZED);
    }
    
    /**
     * 权限不足异常
     */
    public static BusinessException accessDenied() {
        return new BusinessException("ACCESS_DENIED", 
            "权限不足，无法访问该资源", HttpStatus.FORBIDDEN);
    }
}