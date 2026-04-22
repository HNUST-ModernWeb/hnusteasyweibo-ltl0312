package cn.edu.hnust.hnusteasyweibo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 用于表示系统中的用户信息，包含用户的基本属性和状态
 *
 * <p>使用场景：用户注册、登录、个人信息展示、权限管理等业务场景</p>
 *
 * @author hnust-easyweibo
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    /**
     * 用户唯一标识ID
     */
    private long id;

    /**
     * 用户名，用于登录和展示
     */
    private String username;

    /**
     * 密码（加密存储）
     * 使用BCrypt加密，不暴露明文密码
     */
    private String password;

    /**
     * 用户邮箱地址
     */
    private String email;

    /**
     * 个人简介
     * 用户自定义的个人描述信息
     */
    private String bio;

    /**
     * 头像URL
     * 存储用户头像的图片地址
     */
    private String avatar;

    /**
     * 角色
     * 用于权限控制，如"user"、"admin"等
     */
    private String role;

    /**
     * 状态
     * 0-禁用，1-正常
     */
    private short status;

    /**
     * 创建时间
     * 用户注册时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     * 用户信息最后修改时间
     */
    private LocalDateTime updatedAt;
}
