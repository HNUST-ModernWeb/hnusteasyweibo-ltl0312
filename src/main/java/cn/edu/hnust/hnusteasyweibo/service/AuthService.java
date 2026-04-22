package cn.edu.hnust.hnusteasyweibo.service;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 认证服务接口
 * 定义用户认证相关的业务操作方法
 *
 * <p>主要功能：
 * <ul>
 *   <li>登录、注册和登出操作的日志记录</li>
 *   <li>记录用户操作行为用于审计</li>
 * </ul>
 * </p>
 *
 * <p>使用场景：UserController中调用，在用户登录/注册/登出时记录操作日志</p>
 *
 * @author hnust-easyweibo
 * @version 1.0
 */
public interface AuthService {
    /**
     * 记录登录日志
     *
     * <p>功能说明：记录用户登录操作日志，包含成功/失败状态</p>
     *
     * <p>注意事项：
     * <ul>
     *   <li>会自动从请求对象中提取IP地址、User-Agent等信息</li>
     *   <li>登录失败时userId为null，success为false</li>
     * </ul>
     * </p>
     *
     * @param userId 登录用户的ID（登录失败时为null）
     * @param username 登录用户的用户名
     * @param request HTTP请求对象，用于获取客户端信息
     * @param success 登录是否成功
     * @param errorMessage 登录失败时的错误信息（成功时为null）
     */
    void logLogin(Long userId, String username, HttpServletRequest request, boolean success, String errorMessage);

    /**
     * 记录注册日志
     *
     * <p>功能说明：在用户成功注册时记录操作日志，包含用户信息、请求详情等</p>
     *
     * <p>注意事项：
     * <ul>
     *   <li>会自动从请求对象中提取IP地址、User-Agent等信息</li>
     *   <li>会通过AOP记录方法执行时间</li>
     * </ul>
     * </p>
     *
     * @param userId 注册用户的ID
     * @param username 注册用户的用户名
     * @param request HTTP请求对象，用于获取客户端信息
     */
    void logRegister(Long userId, String username, HttpServletRequest request);

    /**
     * 记录登出日志
     *
     * <p>功能说明：在用户登出时记录操作日志</p>
     *
     * @param userId 登出用户的ID
     * @param username 登出用户的用户名
     * @param request HTTP请求对象，用于获取客户端信息
     */
    void logLogout(Long userId, String username, HttpServletRequest request);
}