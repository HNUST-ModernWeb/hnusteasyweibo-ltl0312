package cn.edu.hnust.hnusteasyweibo.service.impl;

import cn.edu.hnust.hnusteasyweibo.constant.LogConstants;
import cn.edu.hnust.hnusteasyweibo.exception.BusinessException;
import cn.edu.hnust.hnusteasyweibo.mapper.FollowMapper;
import cn.edu.hnust.hnusteasyweibo.mapper.UserMapper;
import cn.edu.hnust.hnusteasyweibo.model.Follow;
import cn.edu.hnust.hnusteasyweibo.model.User;
import cn.edu.hnust.hnusteasyweibo.service.FollowService;
import cn.edu.hnust.hnusteasyweibo.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Slf4j
public class FollowServiceImpl implements FollowService {

    @Autowired
    private FollowMapper followMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LogService logService;

    @Override
    @Transactional
    public void follow(long followerId, long followingId) {
        String followerName = getUsername(followerId);
        if (followerId == followingId) {
            logFollowAttempt(followerId, followerName, followingId, LogConstants.OPERATION_TYPE_CREATE, "POST", false, "不能关注自己", 400, LogConstants.REQUEST_STATUS_FAILED);
            throw new BusinessException(400, "不能关注自己");
        }
        Follow existing = followMapper.findByFollowerAndFollowing(followerId, followingId);
        if (existing != null) {
            logFollowAttempt(followerId, followerName, followingId, LogConstants.OPERATION_TYPE_CREATE, "POST", false, "已经关注了该用户", 400, LogConstants.REQUEST_STATUS_FAILED);
            throw new BusinessException(400, "已经关注了该用户");
        }
        Follow follow = new Follow();
        follow.setFollowerId(followerId);
        follow.setFollowingId(followingId);
        follow.setCreatedAt(new Date());
        followMapper.insert(follow);
        logFollowAttempt(followerId, followerName, followingId, LogConstants.OPERATION_TYPE_CREATE, "POST", true, "关注成功", 200, LogConstants.REQUEST_STATUS_SUCCESS);
    }

    @Override
    @Transactional
    public void unfollow(long followerId, long followingId) {
        String followerName = getUsername(followerId);
        Follow existing = followMapper.findByFollowerAndFollowing(followerId, followingId);
        if (existing == null) {
            logFollowAttempt(followerId, followerName, followingId, LogConstants.OPERATION_TYPE_DELETE, "DELETE", false, "未关注该用户", 400, LogConstants.REQUEST_STATUS_FAILED);
            throw new BusinessException(400, "未关注该用户");
        }
        followMapper.delete(followerId, followingId);
        logFollowAttempt(followerId, followerName, followingId, LogConstants.OPERATION_TYPE_DELETE, "DELETE", true, "取消关注成功", 200, LogConstants.REQUEST_STATUS_SUCCESS);
    }

    @Override
    public boolean isFollowing(long followerId, long followingId) {
        return followMapper.findByFollowerAndFollowing(followerId, followingId) != null;
    }

    @Override
    public int getFollowerCount(long userId) {
        return followMapper.countFollowers(userId);
    }

    @Override
    public int getFollowingCount(long userId) {
        return followMapper.countFollowing(userId);
    }

    private void logFollowAttempt(long followerId, String followerName, long followingId, String operationType,
                                  String requestMethod, boolean success, String desc, int statusCode, String requestStatus) {
        logService.logOperation(
                followerId,
                followerName,
                operationType,
                LogConstants.OPERATION_MODULE_FOLLOW,
                desc + "，目标用户ID: " + followingId,
                requestMethod,
                "/api/v1/follows/" + followingId,
                statusCode,
                requestStatus,
                "followerId=" + followerId + ", followingId=" + followingId,
                null,
                0.0
        );
    }

    private String getUsername(long userId) {
        User user = userMapper.findById(userId);
        return user != null ? user.getUsername() : String.valueOf(userId);
    }
}
