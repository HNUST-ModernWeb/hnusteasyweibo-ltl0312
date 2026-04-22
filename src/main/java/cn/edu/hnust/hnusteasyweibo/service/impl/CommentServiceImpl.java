package cn.edu.hnust.hnusteasyweibo.service.impl;

import cn.edu.hnust.hnusteasyweibo.constant.LogConstants;
import cn.edu.hnust.hnusteasyweibo.mapper.CommentMapper;
import cn.edu.hnust.hnusteasyweibo.mapper.UserMapper;
import cn.edu.hnust.hnusteasyweibo.model.Comment;
import cn.edu.hnust.hnusteasyweibo.model.User;
import cn.edu.hnust.hnusteasyweibo.service.CommentService;
import cn.edu.hnust.hnusteasyweibo.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LogService logService;

    @Override
    @Transactional
    public Comment createComment(long postId, long userId, String content) {
        try {
            Comment comment = new Comment();
            comment.setPostId(postId);
            comment.setUserId(userId);
            comment.setContent(content);
            comment.setCreatedAt(new Date());
            comment.setUpdatedAt(new Date());

            commentMapper.insert(comment);
            logCommentOperation(userId, getUsername(userId), postId, comment.getId(), LogConstants.OPERATION_TYPE_CREATE,
                    "POST", true, "发布评论成功", 200, LogConstants.REQUEST_STATUS_SUCCESS, content);
            log.info("用户 {} 成功评论帖子 {}", userId, postId);

            return comment;
        } catch (Exception e) {
            log.error("发布评论失败", e);
            throw new RuntimeException("发布评论失败", e);
        }
    }

    @Override
    public List<Comment> getCommentsByPostId(long postId, int page, int size) {
        try {
            int offset = (page - 1) * size;
            return commentMapper.findByPostId(postId, offset, size);
        } catch (Exception e) {
            log.error("获取评论列表失败", e);
            return null;
        }
    }

    @Override
    public int getCommentCountByPostId(long postId) {
        try {
            return commentMapper.countByPostId(postId);
        } catch (Exception e) {
            log.error("获取评论数失败", e);
            return 0;
        }
    }

    @Override
    @Transactional
    public boolean deleteComment(long commentId, long userId) {
        try {
            String username = getUsername(userId);
            Comment comment = commentMapper.findById(commentId);
            if (comment == null) {
                logCommentOperation(userId, username, null, commentId, LogConstants.OPERATION_TYPE_DELETE,
                        "DELETE", false, "评论不存在", 400, LogConstants.REQUEST_STATUS_FAILED, null);
                log.info("评论 {} 不存在", commentId);
                return false;
            }

            if (comment.getUserId() != userId) {
                logCommentOperation(userId, username, comment.getPostId(), commentId, LogConstants.OPERATION_TYPE_DELETE,
                        "DELETE", false, "无权删除评论", 403, LogConstants.REQUEST_STATUS_FAILED, comment.getContent());
                log.info("用户 {} 无权删除评论 {}", userId, commentId);
                return false;
            }

            commentMapper.deleteById(commentId);
            logCommentOperation(userId, username, comment.getPostId(), commentId, LogConstants.OPERATION_TYPE_DELETE,
                    "DELETE", true, "删除评论成功", 200, LogConstants.REQUEST_STATUS_SUCCESS, comment.getContent());
            log.info("用户 {} 成功删除评论 {}", userId, commentId);
            return true;
        } catch (Exception e) {
            log.error("删除评论失败", e);
            return false;
        }
    }

    @Override
    public List<Comment> getCommentsByUserId(long userId, int page, int size) {
        try {
            int offset = (page - 1) * size;
            return commentMapper.findByUserId(userId, offset, size);
        } catch (Exception e) {
            log.error("获取用户评论列表失败", e);
            return null;
        }
    }

    @Override
    public int getCommentCountByUserId(long userId) {
        try {
            return commentMapper.countByUserId(userId);
        } catch (Exception e) {
            log.error("获取用户评论数失败", e);
            return 0;
        }
    }

    private void logCommentOperation(long userId, String username, Long postId, Long commentId, String operationType,
                                     String requestMethod, boolean success, String desc, int statusCode, String requestStatus,
                                     String content) {
        String requestUrl = commentId != null ? "/api/v1/comments/" + commentId : "/api/v1/posts/" + postId + "/comments";
        logService.logOperation(
                userId,
                username,
                operationType,
                LogConstants.OPERATION_MODULE_COMMENT,
                desc + (postId != null ? "，帖子ID: " + postId : "") + (commentId != null ? "，评论ID: " + commentId : ""),
                requestMethod,
                requestUrl,
                statusCode,
                requestStatus,
                content == null ? "" : content,
                null,
                0.0
        );
    }

    private String getUsername(long userId) {
        User user = userMapper.findById(userId);
        return user != null ? user.getUsername() : String.valueOf(userId);
    }
}
