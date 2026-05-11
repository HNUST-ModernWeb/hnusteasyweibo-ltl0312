package cn.edu.hnust.hnusteasyweibo.service;

import cn.edu.hnust.hnusteasyweibo.model.Comment;

import java.util.List;

/**
 * 评论服务接口
 * 用于处理评论相关的业务逻辑
 */
public interface CommentService {
    /**
     * 发布评论
     */
    Comment createComment(long postId, long userId, String content);

    /**
     * 根据帖子ID获取评论列表
     */
    List<Comment> getCommentsByPostId(long postId, int page, int size);

    /**
     * 根据帖子ID统计评论数
     */
    int getCommentCountByPostId(long postId);

    /**
     * 删除评论
     */
    boolean deleteComment(long commentId, long userId);

    /**
     * 根据用户ID获取评论列表
     */
    List<Comment> getCommentsByUserId(long userId, int page, int size);

    /**
     * 根据用户ID统计评论数
     */
    int getCommentCountByUserId(long userId);
}
