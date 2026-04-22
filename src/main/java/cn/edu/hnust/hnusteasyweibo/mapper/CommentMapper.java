package cn.edu.hnust.hnusteasyweibo.mapper;

import cn.edu.hnust.hnusteasyweibo.model.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {
    @Insert("INSERT INTO comments (post_id, user_id, content, created_at, updated_at) VALUES (#{postId}, #{userId}, #{content}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Comment comment);

    @Select("SELECT c.*, u.username FROM comments c LEFT JOIN users u ON c.user_id = u.id WHERE c.post_id = #{postId} ORDER BY c.created_at DESC LIMIT #{offset}, #{limit}")
    List<Comment> findByPostId(@Param("postId") long postId, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM comments WHERE post_id = #{postId}")
    int countByPostId(long postId);

    @Delete("DELETE FROM comments WHERE id = #{id}")
    int deleteById(long id);

    @Select("SELECT * FROM comments WHERE id = #{id}")
    Comment findById(long id);

    @Select("SELECT * FROM comments WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    List<Comment> findByUserId(@Param("userId") long userId, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM comments WHERE user_id = #{userId}")
    int countByUserId(long userId);
}
