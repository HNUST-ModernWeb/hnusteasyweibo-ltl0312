package cn.edu.hnust.hnusteasyweibo.service;

import cn.edu.hnust.hnusteasyweibo.dto.RegisterDTO;
import cn.edu.hnust.hnusteasyweibo.dto.UserDTO;
import cn.edu.hnust.hnusteasyweibo.model.User;

import java.util.Map;

/**
 * 用户服务接口
 * 定义用户相关的业务操作方法
 *
 * <p>主要功能：
 * <ul>
 *   <li>用户注册和登录</li>
 *   <li>用户信息查询和更新</li>
 *   <li>用户状态管理（启用/禁用）</li>
 * </ul>
 * </p>
 *
 * <p>使用场景：UserController中调用，处理用户相关的业务逻辑</p>
 *
 * @author hnust-easyweibo
 * @version 1.0
 */
public interface UserService {
    /**
     * 用户注册
     *
     * <p>功能说明：处理新用户注册逻辑，包括密码加密、用户信息保存等</p>
     *
     * <p>注意事项：
     * <ul>
     *   <li>注册时自动使用BCrypt加密密码</li>
     *   <li>默认角色为"user"，状态为正常</li>
     *   <li>需校验用户名和邮箱的唯一性</li>
     * </ul>
     * </p>
     *
     * @param registerDTO 注册信息，包含用户名、密码、邮箱
     * @return 注册成功的用户对象
     * @throws BusinessException 若用户名或邮箱已存在
     */
    User register(RegisterDTO registerDTO);

    /**
     * 用户登录
     *
     * <p>功能说明：验证用户登录凭证，返回登录成功的用户信息</p>
     *
     * <p>注意事项：
     * <ul>
     *   <li>密码使用BCrypt加密存储，验证时自动比对</li>
     *   <li>登录成功不会自动生成Token，由调用方处理</li>
     * </ul>
     * </p>
     *
     * @param username 用户名
     * @param password 明文密码
     * @return 登录成功的用户对象
     * @throws BusinessException 若用户名或密码错误
     */
    User login(String username, String password);

    /**
     * 根据ID获取用户
     *
     * <p>功能说明：根据用户唯一ID查询用户完整信息</p>
     *
     * @param id 用户唯一标识ID
     * @return 用户对象，若不存在返回null
     */
    User getUserById(Long id);

    /**
     * 根据用户名获取用户
     *
     * <p>功能说明：根据用户名精确查询用户信息</p>
     *
     * @param username 用户名
     * @return 用户对象，若不存在返回null
     */
    User getUserByUsername(String username);

    /**
     * 根据邮箱获取用户
     *
     * <p>功能说明：根据用户邮箱精确查询用户信息</p>
     *
     * @param email 邮箱地址
     * @return 用户对象，若不存在返回null
     */
    User getUserByEmail(String email);

    /**
     * 更新用户信息
     *
     * <p>功能说明：更新指定用户的个人信息和设置</p>
     *
     * <p>注意事项：
     * <ul>
     *   <li>只更新UserDTO中非null的字段</li>
     *   <li>密码更新需特殊处理，不通过此方法</li>
     * </ul>
     * </p>
     *
     * @param id 用户ID
     * @param userDTO 更新信息，只需传入需要更新的字段
     * @return 更新后的用户对象
     */
    User updateUser(Long id, UserDTO userDTO);

    /**
     * 禁用用户
     *
     * <p>功能说明：将指定用户的状态设置为禁用（status=0）</p>
     *
     * <p>注意事项：禁用后用户将无法登录系统</p>
     *
     * @param id 用户ID
     */
    void disableUser(Long id);

    /**
     * 启用用户
     *
     * <p>功能说明：将指定用户的状态设置为正常（status=1）</p>
     *
     * @param id 用户ID
     */
    void enableUser(Long id);

    /**
     * 获取用户统计信息
     *
     * <p>功能说明：获取用户的统计信息，包括帖子数、评论数、点赞数等</p>
     *
     * @param userId 用户ID
     * @return 包含用户统计信息的Map
     */
    Map<String, Integer> getUserStatistics(Long userId);
}
