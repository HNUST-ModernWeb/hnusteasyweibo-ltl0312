package cn.edu.hnust.hnusteasyweibo.dto;

import lombok.Data;

/**
 * 登录请求DTO
 * 用于接收用户登录请求的参数
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
