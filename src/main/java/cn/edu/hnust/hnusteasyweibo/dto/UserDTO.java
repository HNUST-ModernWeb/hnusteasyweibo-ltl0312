package cn.edu.hnust.hnusteasyweibo.dto;

import lombok.Data;

/**
 * 用户DTO（Data Transfer Object）
 * 用于在前后端交互中传输用户信息，不包含敏感数据如密码
 *
 * <p>与User实体的区别：UserDTO用于API响应，不包含password等敏感字段</p>
 *
 * <p>使用场景：用户信息更新接口、个人信息展示等</p>
 *
 * @author hnust-easyweibo
 * @version 1.0
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

