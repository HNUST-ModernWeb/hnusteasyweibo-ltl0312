package cn.edu.hnust.hnusteasyweibo.controller;

import cn.edu.hnust.hnusteasyweibo.constant.ServerConstants;
import cn.edu.hnust.hnusteasyweibo.dto.LoginDTO;
import cn.edu.hnust.hnusteasyweibo.dto.RegisterDTO;
import cn.edu.hnust.hnusteasyweibo.dto.ResponseDTO;
import cn.edu.hnust.hnusteasyweibo.dto.UserDTO;
import cn.edu.hnust.hnusteasyweibo.dto.UserProfileVO;
import cn.edu.hnust.hnusteasyweibo.model.User;
import cn.edu.hnust.hnusteasyweibo.security.JwtTokenProvider;
import cn.edu.hnust.hnusteasyweibo.service.AuthService;
import cn.edu.hnust.hnusteasyweibo.service.FollowService;
import cn.edu.hnust.hnusteasyweibo.service.PostService;
import cn.edu.hnust.hnusteasyweibo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(ServerConstants.API_BASE_PATH + ServerConstants.USERS_PATH)
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private FollowService followService;

    @Autowired
    private PostService postService;

    @PostMapping(ServerConstants.REGISTER_PATH)
    public ResponseDTO<?> register(@RequestBody RegisterDTO registerDTO, HttpServletRequest request) {
        User user = userService.register(registerDTO);
        authService.logRegister(user.getId(), user.getUsername(), request);
        String token = jwtTokenProvider.generateToken(user);
        return ResponseDTO.success("注册成功");
    }

    @PostMapping(ServerConstants.LOGIN_PATH)
    public ResponseDTO<?> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        try {
            User user = userService.login(loginDTO.getUsername(), loginDTO.getPassword());
            String token = jwtTokenProvider.generateToken(user);
            authService.logLogin(user.getId(), user.getUsername(), request, true, null);
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user);
            return ResponseDTO.success(data);
        } catch (Exception e) {
            User existingUser = userService.getUserByUsername(loginDTO.getUsername());
            Long userId = existingUser != null ? existingUser.getId() : null;
            String username = existingUser != null ? existingUser.getUsername() : loginDTO.getUsername();
            authService.logLogin(userId, username, request, false, e.getMessage());
            throw e;
        }
    }

    @PostMapping(ServerConstants.LOGOUT_PATH)
    public ResponseDTO<?> logout(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseDTO.error(401, "未授权，请先登录");
        }
        User user = userService.getUserById(userId);
        authService.logLogout(user.getId(), user.getUsername(), request);
        return ResponseDTO.success("退出登录成功");
    }

    @GetMapping(ServerConstants.PROFILE_PATH)
    public ResponseDTO<?> getProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getUserById(userId);
        user.setPassword(null);
        return ResponseDTO.success(user);
    }

    @PutMapping(ServerConstants.PROFILE_PATH)
    public ResponseDTO<?> updateProfile(@RequestBody UserDTO userDTO, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.updateUser(userId, userDTO);
        user.setPassword(null);
        return ResponseDTO.success(user);
    }

    @GetMapping(ServerConstants.USER_BY_ID_PATH)
    public ResponseDTO<?> getUser(@PathVariable Long id, HttpServletRequest request) {
        User user = userService.getUserById(id);
        user.setPassword(null);

        Long currentUserId = (Long) request.getAttribute("userId");
        boolean isFollowing = false;
        if (currentUserId != null) {
            isFollowing = followService.isFollowing(currentUserId, id);
        }

        UserProfileVO profileVO = new UserProfileVO();
        profileVO.setId(user.getId());
        profileVO.setUsername(user.getUsername());
        profileVO.setEmail(user.getEmail());
        profileVO.setBio(user.getBio());
        profileVO.setAvatar(user.getAvatar());
        profileVO.setRole(user.getRole());
        profileVO.setFollowerCount(followService.getFollowerCount(id));
        profileVO.setFollowingCount(followService.getFollowingCount(id));
        profileVO.setPostCount(postService.getPostCountByUser(id));
        profileVO.setFollowing(isFollowing);

        return ResponseDTO.success(profileVO);
    }

    @GetMapping("/statistics")
    public ResponseDTO<?> getUserStatistics(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Map<String, Integer> statistics = userService.getUserStatistics(userId);
        statistics.put("followerCount", followService.getFollowerCount(userId));
        statistics.put("followingCount", followService.getFollowingCount(userId));
        return ResponseDTO.success(statistics);
    }
}
