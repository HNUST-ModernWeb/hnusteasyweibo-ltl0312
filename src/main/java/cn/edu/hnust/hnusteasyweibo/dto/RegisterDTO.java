package cn.edu.hnust.hnusteasyweibo.dto;

import lombok.Data;

/**
 * 注册请求DTO（Data Transfer Object）
 * 用于接收用户注册请求的参数
 *
 * <p>请求示例：
 * <pre>
 * {
 *     "username": "user123",
 *     "password": "password123",
 *     "email": "user@example.com"
 * }
 * </pre>
 * </p>
 *
 * <p>使用场景：用户注册接口 /api/v1/users/register</p>
 *
 * @author hnust-easyweibo
 * @version 1.0
 */
@Data
public class RegisterDTO {
    /**
     * 用户名
     * 用于登录识别的用户名，需唯一
     */
    private String username;

    /**
     * 密码
     * 用户设置的登录密码
     */
    private String password;

    /**
     * 邮箱
     * 用户的邮箱地址，需唯一
     */
    private String email;
}
