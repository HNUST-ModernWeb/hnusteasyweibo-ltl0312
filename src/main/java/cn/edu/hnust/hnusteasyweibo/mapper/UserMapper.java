package cn.edu.hnust.hnusteasyweibo.mapper;

import cn.edu.hnust.hnusteasyweibo.model.User;
import org.apache.ibatis.annotations.*;

/**
 * 用户Mapper接口
 * 提供用户数据的数据库操作方法，支持按ID、用户名、邮箱查询用户
 */
@Mapper
public interface UserMapper {
    /**
     * 插入用户
     */
    @Insert("INSERT INTO users (username, password, email, bio, avatar, role, status, created_at, updated_at) VALUES (#{username}, #{password}, #{email}, #{bio}, #{avatar}, #{role}, #{status}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);

    /**
     * 更新用户
     */
    @Update("UPDATE users SET username = #{username}, password = #{password}, email = #{email}, bio = #{bio}, avatar = #{avatar}, role = #{role}, status = #{status}, updated_at = #{updatedAt} WHERE id = #{id}")
    void update(User user);

    /**
     * 根据ID查找用户
     */
    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(Long id);

    /**
     * 根据用户名查找用户
     */
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);

    /**
     * 根据邮箱查找用户
     */
    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(String email);

    /**
     * 删除用户
     */
    @Delete("DELETE FROM users WHERE id = #{id}")
    void delete(Long id);
}
