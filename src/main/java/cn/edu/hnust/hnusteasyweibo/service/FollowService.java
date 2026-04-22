package cn.edu.hnust.hnusteasyweibo.service;

public interface FollowService {
    void follow(long followerId, long followingId);
    void unfollow(long followerId, long followingId);
    boolean isFollowing(long followerId, long followingId);
    int getFollowerCount(long userId);
    int getFollowingCount(long userId);
}
