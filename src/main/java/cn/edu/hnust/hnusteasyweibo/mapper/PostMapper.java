package cn.edu.hnust.hnusteasyweibo.mapper;

import cn.edu.hnust.hnusteasyweibo.model.Post;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PostMapper {
    @Insert("INSERT INTO posts (user_id, category_id, content, image, created_at, updated_at) " +
            "VALUES (#{userId}, #{categoryId}, #{content}, #{image}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Post post);

    @Update("UPDATE posts SET category_id = #{categoryId}, content = #{content}, image = #{image}, updated_at = #{updatedAt} WHERE id = #{id}")
    void update(Post post);

    @Select("SELECT p.*, c.name as category_name FROM posts p LEFT JOIN categories c ON p.category_id = c.id WHERE p.id = #{id}")
    Post findById(Long id);

    @Delete("DELETE FROM posts WHERE id = #{id}")
    void delete(Long id);

    @Select("SELECT p.*, c.name as category_name FROM posts p LEFT JOIN categories c ON p.category_id = c.id ORDER BY p.created_at DESC LIMIT #{offset}, #{limit}")
    List<Post> findAll(@Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT p.*, c.name as category_name FROM posts p LEFT JOIN categories c ON p.category_id = c.id WHERE p.category_id = #{categoryId} ORDER BY p.created_at DESC LIMIT #{offset}, #{limit}")
    List<Post> findByCategoryId(@Param("categoryId") Long categoryId, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT p.*, c.name as category_name FROM posts p LEFT JOIN categories c ON p.category_id = c.id WHERE p.user_id = #{userId} ORDER BY p.created_at DESC LIMIT #{offset}, #{limit}")
    List<Post> findByUserId(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM posts")
    int countAll();

    @Select("SELECT COUNT(*) FROM posts WHERE category_id = #{categoryId}")
    int countByCategoryId(Long categoryId);

    @Select("SELECT COUNT(*) FROM posts WHERE user_id = #{userId}")
    int countByUserId(Long userId);

    @Update("UPDATE posts SET view_count = IFNULL(view_count, 0) + 1 WHERE id = #{id}")
    void incrementViewCount(Long id);

    @Update("UPDATE posts SET comments_count = #{commentsCount} WHERE id = #{id}")
    void updateCommentsCount(@Param("id") Long id, @Param("commentsCount") int commentsCount);

    @Update("UPDATE posts SET likes_count = #{likesCount} WHERE id = #{id}")
    void updateLikesCount(@Param("id") Long id, @Param("likesCount") int likesCount);

    @Select("SELECT COUNT(*) FROM likes WHERE post_id = #{postId}")
    int countLikesForPost(Long postId);

    @Select("SELECT COUNT(*) FROM comments WHERE post_id = #{postId}")
    int countCommentsForPost(Long postId);

    @Select("SELECT p.*, c.name as category_name FROM posts p LEFT JOIN categories c ON p.category_id = c.id " +
            "WHERE p.content LIKE #{keyword} OR p.user_id IN (SELECT id FROM users WHERE username LIKE #{keyword}) " +
            "ORDER BY p.created_at DESC LIMIT #{offset}, #{limit}")
    List<Post> searchPosts(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM posts p " +
            "WHERE p.content LIKE #{keyword} OR p.user_id IN (SELECT id FROM users WHERE username LIKE #{keyword})")
    int countSearchPosts(@Param("keyword") String keyword);
}
