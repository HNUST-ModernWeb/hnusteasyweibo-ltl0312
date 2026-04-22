package cn.edu.hnust.hnusteasyweibo.mapper;

import cn.edu.hnust.hnusteasyweibo.model.User;
import org.apache.ibatis.annotations.*;

/**
 * 用户Mapper接口
 * 提供用户数据的数据库操作方法
 *
 * <p>主要功能：
 * <ul>
 *   <li>用户数据的增删改查操作</li>
 *   <li>支持按ID、用户名、邮箱查询用户</li>
 * </ul>
 * </p>
 *
 * <p>使用场景：UserService实现类中调用，完成用户相关的数据库操作</p>
 *
 * @author hnust-easyweibo
 * @version 1.0
 */
@Mapper
public interface UserMapper {
    /**
     * 插入用户
     *
     * <p>功能说明：将新用户信息插入数据库</p>
     *
     * @param user 用户信息对象，包含用户名、密码、邮箱等
     */
    @Insert("INSERT INTO users (username, password, email, bio, avatar, role, status, created_at, updated_at) VALUES (#{username}, #{password}, #{email}, #{bio}, #{avatar}, #{role}, #{status}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);

    /**
     * 更新用户
     *
     * <p>功能说明：根据用户ID更新用户信息</p>
     *
     * @param user 用户信息对象，包含要更新的字段值
     */
    @Update("UPDATE users SET username = #{username}, password = #{password}, email = #{email}, bio = #{bio}, avatar = #{avatar}, role = #{role}, status = #{status}, updated_at = #{updatedAt} WHERE id = #{id}")
    void update(User user);

    /**
     * 根据ID查找用户
     *
     * <p>功能说明：根据用户唯一ID查询用户完整信息</p>
     *
     * @param id 用户唯一标识ID
     * @return 用户信息对象，若不存在返回null
     */
    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(Long id);

    /**
     * 根据用户名查找用户
     *
     * <p>功能说明：根据用户名精确查询用户信息</p>
     *
     * @param username 用户名
     * @return 用户信息对象，若不存在返回null
     */
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);

    /**
     * 根据邮箱查找用户
     *
     * <p>功能说明：根据用户邮箱精确查询用户信息</p>
     *
     * @param email 邮箱地址
     * @return 用户信息对象，若不存在返回null
     */
    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(String email);

    /**
     * 删除用户
     *
     * <p>功能说明：根据用户ID删除用户（物理删除）</p>
     *
     * @param id 用户唯一标识ID
     */
    @Delete("DELETE FROM users WHERE id = #{id}")
    void delete(Long id);
}
