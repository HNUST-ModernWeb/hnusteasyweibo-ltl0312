package cn.edu.hnust.hnusteasyweibo.service;

/**
 * 点赞服务接口
 * 用于处理点赞相关的业务逻辑
 *
 * @author hnust-easyweibo
 * @version 1.0
 */
public interface LikeService {
    /**
     * 点赞帖子
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 点赞后的点赞数
     */
    int likePost(long postId, long userId);

    /**
     * 取消点赞帖子
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 取消点赞后的点赞数
     */
    int unlikePost(long postId, long userId);

    /**
     * 检查用户是否已点赞帖子
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否已点赞
     */
    boolean isLiked(long postId, long userId);

    /**
     * 获取帖子的点赞数
     *
     * @param postId 帖子ID
     * @return 点赞数
     */
    int getLikeCount(long postId);

    /**
     * 统计用户获得的点赞数量
     *
     * @param userId 用户ID
     * @return 点赞数
     */
    int countLikesByUserId(long userId);
}
