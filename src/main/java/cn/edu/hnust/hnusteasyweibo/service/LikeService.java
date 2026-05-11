package cn.edu.hnust.hnusteasyweibo.service;

/**
 * 点赞服务接口
 * 用于处理点赞相关的业务逻辑
 */
public interface LikeService {
    /**
     * 点赞帖子
     */
    int likePost(long postId, long userId);

    /**
     * 取消点赞帖子
     */
    int unlikePost(long postId, long userId);

    /**
     * 检查用户是否已点赞帖子
     */
    boolean isLiked(long postId, long userId);

    /**
     * 获取帖子的点赞数
     */
    int getLikeCount(long postId);

    /**
     * 统计用户获得的点赞数量
     */
    int countLikesByUserId(long userId);
}
