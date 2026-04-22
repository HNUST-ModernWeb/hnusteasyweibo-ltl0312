package cn.edu.hnust.hnusteasyweibo.dto;

import lombok.Data;

/**
 * 登录请求DTO（Data Transfer Object）
 * 用于接收用户登录请求的参数
 *
 * <p>请求示例：
 * <pre>
 * {
 *     "username": "user123",
 *     "password": "password123"
 * }
 * </pre>
 * </p>
 *
 * <p>使用场景：用户登录接口 /api/v1/users/login</p>
 *
 * @author hnust-easyweibo
 * @version 1.0
 */
@Data
public class LoginDTO {
    /**
     * 用户名
     * 用于登录识别的用户名
     */
    private String username;

    /**
     * 密码
     * 用户登录密码
     */
    private String password;
}
