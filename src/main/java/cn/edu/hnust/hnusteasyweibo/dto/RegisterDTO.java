package cn.edu.hnust.hnusteasyweibo.dto;

import lombok.Data;

/**
 * 注册请求DTO
 * 用于接收用户注册请求的参数
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
