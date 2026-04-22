package cn.edu.hnust.hnusteasyweibo.mapper;

import cn.edu.hnust.hnusteasyweibo.model.Like;
import org.apache.ibatis.annotations.*;

/**
 * 点赞数据访问接口
 * 用于处理点赞相关的数据库操作
 *
 * @author hnust-easyweibo
 * @version 1.0
 */
@Mapper
public interface LikeMapper {
    /**
     * 插入点赞记录
     *
     * @param like 点赞实体对象
     * @return 影响的行数
     */
    @Insert("INSERT INTO likes (post_id, user_id, created_at) VALUES (#{postId}, #{userId}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Like like);

    /**
     * 删除点赞记录
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM likes WHERE post_id = #{postId} AND user_id = #{userId}")
    int deleteByPostIdAndUserId(@Param("postId") long postId, @Param("userId") long userId);

    /**
     * 根据帖子ID和用户ID查询点赞记录
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 点赞实体对象，若不存在则返回null
     */
    @Select("SELECT * FROM likes WHERE post_id = #{postId} AND user_id = #{userId}")
    Like findByPostIdAndUserId(@Param("postId") long postId, @Param("userId") long userId);

    /**
     * 根据帖子ID统计点赞数
     *
     * @param postId 帖子ID
     * @return 点赞数
     */
    @Select("SELECT COUNT(*) FROM likes WHERE post_id = #{postId}")
    int countByPostId(long postId);

    /**
     * 统计用户获得的点赞数量
     *
     * @param userId 用户ID
     * @return 点赞数
     */
    @Select("SELECT COUNT(*) FROM likes l JOIN posts p ON l.post_id = p.id WHERE p.user_id = #{userId}")
    int countByUserId(long userId);
}
