package cn.edu.hnust.hnusteasyweibo.dto;

import lombok.Data;

/**
 * 用户DTO
 * 用于前后端交互中传输用户信息，不包含敏感数据
 */
@Data
public class UserDTO {
    /**
     * 用户名
     * 用户登录时使用的名称
     */
    private String username;

    /**
     * 邮箱
     * 用户的邮箱地址
     */
    private String email;

    /**
     * 个人简介
     * 用户的个人描述信息
     */
    private String bio;

    /**
     * 头像URL
     * 用户头像的图片地址
     */
    private String avatar;
}

