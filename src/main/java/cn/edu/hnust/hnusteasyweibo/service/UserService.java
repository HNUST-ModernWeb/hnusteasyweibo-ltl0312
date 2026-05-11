package cn.edu.hnust.hnusteasyweibo.service;

import cn.edu.hnust.hnusteasyweibo.dto.RegisterDTO;
import cn.edu.hnust.hnusteasyweibo.dto.UserDTO;
import cn.edu.hnust.hnusteasyweibo.model.User;

import java.util.Map;

/**
 * 用户服务接口
 * 定义用户相关的业务操作方法
 */
public interface UserService {
    /**
     * 用户注册
     */
    User register(RegisterDTO registerDTO);

    /**
     * 用户登录
     */
    User login(String username, String password);

    /**
     * 根据ID获取用户
     */
    User getUserById(Long id);

    /**
     * 根据用户名获取用户
     */
    User getUserByUsername(String username);

    /**
     * 根据邮箱获取用户
     */
    User getUserByEmail(String email);

    /**
     * 更新用户信息
     */
    User updateUser(Long id, UserDTO userDTO);

    /**
     * 禁用用户
     */
    void disableUser(Long id);

    /**
     * 启用用户
     */
    void enableUser(Long id);

    /**
     * 获取用户统计信息
     */
    Map<String, Integer> getUserStatistics(Long userId);
}
