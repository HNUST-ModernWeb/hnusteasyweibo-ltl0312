package cn.edu.hnust.hnusteasyweibo.service;

import cn.edu.hnust.hnusteasyweibo.model.Comment;

import java.util.List;

/**
 * 评论服务接口
 * 用于处理评论相关的业务逻辑
 *
 * @author hnust-easyweibo
 * @version 1.0
 */
public interface CommentService {
    /**
     * 发布评论
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @param content 评论内容
     * @return 发布的评论
     */
    Comment createComment(long postId, long userId, String content);

    /**
     * 根据帖子ID获取评论列表
     *
     * @param postId 帖子ID
     * @param page 页码
     * @param size 每页数量
     * @return 评论列表
     */
    List<Comment> getCommentsByPostId(long postId, int page, int size);

    /**
     * 根据帖子ID统计评论数
     *
     * @param postId 帖子ID
     * @return 评论数
     */
    int getCommentCountByPostId(long postId);

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteComment(long commentId, long userId);

    /**
     * 根据用户ID获取评论列表
     *
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页数量
     * @return 评论列表
     */
    List<Comment> getCommentsByUserId(long userId, int page, int size);

    /**
     * 根据用户ID统计评论数
     *
     * @param userId 用户ID
     * @return 评论数
     */
    int getCommentCountByUserId(long userId);
}
