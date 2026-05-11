package cn.edu.hnust.hnusteasyweibo.service;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 认证服务接口
 * 定义用户登录、注册和登出操作的日志记录方法
 */
public interface AuthService {
    /**
     * 记录登录日志
     */
    void logLogin(Long userId, String username, HttpServletRequest request, boolean success, String errorMessage);

    /**
     * 记录注册日志
     */
    void logRegister(Long userId, String username, HttpServletRequest request);

    /**
     * 记录登出日志
     */
    void logLogout(Long userId, String username, HttpServletRequest request);
}