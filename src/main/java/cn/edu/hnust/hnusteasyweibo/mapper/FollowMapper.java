package cn.edu.hnust.hnusteasyweibo.mapper;

import cn.edu.hnust.hnusteasyweibo.model.Follow;
import org.apache.ibatis.annotations.*;

@Mapper
public interface FollowMapper {
    @Insert("INSERT INTO follows (follower_id, following_id, created_at) VALUES (#{followerId}, #{followingId}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Follow follow);

    @Delete("DELETE FROM follows WHERE follower_id = #{followerId} AND following_id = #{followingId}")
    int delete(@Param("followerId") long followerId, @Param("followingId") long followingId);

    @Select("SELECT * FROM follows WHERE follower_id = #{followerId} AND following_id = #{followingId}")
    Follow findByFollowerAndFollowing(@Param("followerId") long followerId, @Param("followingId") long followingId);

    @Select("SELECT COUNT(*) FROM follows WHERE following_id = #{userId}")
    int countFollowers(long userId);

    @Select("SELECT COUNT(*) FROM follows WHERE follower_id = #{userId}")
    int countFollowing(long userId);

    @Select("SELECT * FROM follows WHERE follower_id = #{followerId}")
    java.util.List<Follow> findFollowingByUserId(@Param("followerId") long followerId);
}
