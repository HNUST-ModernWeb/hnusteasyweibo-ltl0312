package cn.edu.hnust.hnusteasyweibo.controller;

import cn.edu.hnust.hnusteasyweibo.dto.ResponseDTO;
import cn.edu.hnust.hnusteasyweibo.service.FollowService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/follows")
@Slf4j
public class FollowController {

    @Autowired
    private FollowService followService;

    @PostMapping("/{userId}")
    public ResponseDTO<?> follow(@PathVariable Long userId, HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        if (currentUserId == null) {
            return ResponseDTO.error(401, "未授权，请先登录");
        }
        try {
            followService.follow(currentUserId, userId);
            return ResponseDTO.success("关注成功");
        } catch (Exception e) {
            return ResponseDTO.error(400, e.getMessage());
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseDTO<?> unfollow(@PathVariable Long userId, HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        if (currentUserId == null) {
            return ResponseDTO.error(401, "未授权，请先登录");
        }
        try {
            followService.unfollow(currentUserId, userId);
            return ResponseDTO.success("取消关注成功");
        } catch (Exception e) {
            return ResponseDTO.error(400, e.getMessage());
        }
    }

    @GetMapping("/check/{userId}")
    public ResponseDTO<?> checkFollowing(@PathVariable Long userId, HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        if (currentUserId == null) {
            return ResponseDTO.success(false);
        }
        boolean isFollowing = followService.isFollowing(currentUserId, userId);
        return ResponseDTO.success(isFollowing);
    }

    @GetMapping("/{userId}/stats")
    public ResponseDTO<?> getFollowStats(@PathVariable Long userId) {
        int followerCount = followService.getFollowerCount(userId);
        int followingCount = followService.getFollowingCount(userId);
        java.util.Map<String, Integer> stats = new java.util.HashMap<>();
        stats.put("followerCount", followerCount);
        stats.put("followingCount", followingCount);
        return ResponseDTO.success(stats);
    }
}
