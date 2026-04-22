package cn.edu.hnust.hnusteasyweibo.service.impl;

import cn.edu.hnust.hnusteasyweibo.constant.LogConstants;
import cn.edu.hnust.hnusteasyweibo.mapper.LikeMapper;
import cn.edu.hnust.hnusteasyweibo.mapper.UserMapper;
import cn.edu.hnust.hnusteasyweibo.model.Like;
import cn.edu.hnust.hnusteasyweibo.model.User;
import cn.edu.hnust.hnusteasyweibo.service.LikeService;
import cn.edu.hnust.hnusteasyweibo.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Slf4j
public class LikeServiceImpl implements LikeService {

    @Autowired
    private LikeMapper likeMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LogService logService;

    @Override
    @Transactional
    public int likePost(long postId, long userId) {
        try {
            String username = getUsername(userId);
            Like existingLike = likeMapper.findByPostIdAndUserId(postId, userId);
            if (existingLike != null) {
                logLikeOperation(userId, username, postId, LogConstants.OPERATION_TYPE_CREATE, "POST", false, "用户已点赞该帖子", 400, LogConstants.REQUEST_STATUS_FAILED);
                return likeMapper.countByPostId(postId);
            }

            Like like = new Like();
            like.setPostId(postId);
            like.setUserId(userId);
            like.setCreatedAt(new Date());

            likeMapper.insert(like);
            logLikeOperation(userId, username, postId, LogConstants.OPERATION_TYPE_CREATE, "POST", true, "点赞成功", 200, LogConstants.REQUEST_STATUS_SUCCESS);
            return likeMapper.countByPostId(postId);
        } catch (Exception e) {
            log.error("点赞失败", e);
            throw new RuntimeException("点赞失败", e);
        }
    }

    @Override
    @Transactional
    public int unlikePost(long postId, long userId) {
        try {
            String username = getUsername(userId);
            Like existingLike = likeMapper.findByPostIdAndUserId(postId, userId);
            if (existingLike == null) {
                logLikeOperation(userId, username, postId, LogConstants.OPERATION_TYPE_DELETE, "POST", false, "用户未点赞该帖子", 400, LogConstants.REQUEST_STATUS_FAILED);
                return likeMapper.countByPostId(postId);
            }

            likeMapper.deleteByPostIdAndUserId(postId, userId);
            logLikeOperation(userId, username, postId, LogConstants.OPERATION_TYPE_DELETE, "POST", true, "取消点赞成功", 200, LogConstants.REQUEST_STATUS_SUCCESS);
            return likeMapper.countByPostId(postId);
        } catch (Exception e) {
            log.error("取消点赞失败", e);
            throw new RuntimeException("取消点赞失败", e);
        }
    }

    @Override
    public boolean isLiked(long postId, long userId) {
        try {
            Like like = likeMapper.findByPostIdAndUserId(postId, userId);
            return like != null;
        } catch (Exception e) {
            log.error("检查点赞状态失败", e);
            return false;
        }
    }

    @Override
    public int getLikeCount(long postId) {
        try {
            return likeMapper.countByPostId(postId);
        } catch (Exception e) {
            log.error("获取点赞数失败", e);
            return 0;
        }
    }

    @Override
    public int countLikesByUserId(long userId) {
        try {
            return likeMapper.countByUserId(userId);
        } catch (Exception e) {
            log.error("统计用户获得的点赞数量失败", e);
            return 0;
        }
    }

    private void logLikeOperation(long userId, String username, long postId, String operationType, String requestMethod,
                                  boolean success, String desc, int statusCode, String requestStatus) {
        logService.logOperation(
                userId,
                username,
                operationType,
                LogConstants.OPERATION_MODULE_LIKE,
                desc + "，帖子ID: " + postId,
                requestMethod,
                "/api/v1/posts/" + postId + (LogConstants.OPERATION_TYPE_DELETE.equals(operationType) ? "/unlike" : "/like"),
                statusCode,
                requestStatus,
                "postId=" + postId + ", userId=" + userId,
                null,
                0.0
        );
    }

    private String getUsername(long userId) {
        User user = userMapper.findById(userId);
        return user != null ? user.getUsername() : String.valueOf(userId);
    }
}
