package cn.edu.hnust.hnusteasyweibo.service.impl;

import cn.edu.hnust.hnusteasyweibo.constant.LogConstants;
import cn.edu.hnust.hnusteasyweibo.dto.RegisterDTO;
import cn.edu.hnust.hnusteasyweibo.dto.UserDTO;
import cn.edu.hnust.hnusteasyweibo.exception.BusinessException;
import cn.edu.hnust.hnusteasyweibo.mapper.UserMapper;
import cn.edu.hnust.hnusteasyweibo.model.User;
import cn.edu.hnust.hnusteasyweibo.service.CommentService;
import cn.edu.hnust.hnusteasyweibo.service.LikeService;
import cn.edu.hnust.hnusteasyweibo.service.LogService;
import cn.edu.hnust.hnusteasyweibo.service.PostService;
import cn.edu.hnust.hnusteasyweibo.service.UserService;
import cn.edu.hnust.hnusteasyweibo.utils.PasswordEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private LogService logService;

    @Override
    @Transactional
    public User register(RegisterDTO registerDTO) {
        if (userMapper.findByUsername(registerDTO.getUsername()) != null) {
            throw new BusinessException(400, "用户名已存在");
        }

        if (userMapper.findByEmail(registerDTO.getEmail()) != null) {
            throw new BusinessException(400, "邮箱已被注册");
        }

        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setEmail(registerDTO.getEmail());
        user.setRole("user");
        user.setStatus((short) 1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userMapper.insert(user);
        logService.ensureUserDirectory(user.getId());
        log.info("用户注册成功: {}", registerDTO.getUsername());
        return user;
    }

    @Override
    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        if (user.getStatus() == 0) {
            throw new BusinessException(403, "账号已被禁用");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        log.info("用户登录成功: {}", username);
        return user;
    }

    @Override
    public User getUserById(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return user;
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public User getUserByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    @Override
    @Transactional
    public User updateUser(Long id, UserDTO userDTO) {
        User user = getUserById(id);

        BeanUtils.copyProperties(userDTO, user, "id", "password", "role", "status", "createdAt");
        user.setUpdatedAt(LocalDateTime.now());

        userMapper.update(user);
        logService.logOperation(
                user.getId(),
                user.getUsername(),
                LogConstants.OPERATION_TYPE_UPDATE,
                LogConstants.OPERATION_MODULE_USER,
                "修改个人信息",
                "PUT",
                "/api/v1/users/profile",
                200,
                LogConstants.REQUEST_STATUS_SUCCESS,
                buildUserUpdateParams(userDTO),
                null,
                0.0
        );
        log.info("用户信息更新成功: {}", user.getUsername());
        return user;
    }

    @Override
    @Transactional
    public void disableUser(Long id) {
        User user = getUserById(id);
        user.setStatus((short) 0);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.update(user);
        log.info("用户已禁用: {}", user.getUsername());
    }

    @Override
    @Transactional
    public void enableUser(Long id) {
        User user = getUserById(id);
        user.setStatus((short) 1);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.update(user);
        log.info("用户已启用: {}", user.getUsername());
    }

    @Override
    public Map<String, Integer> getUserStatistics(Long userId) {
        Map<String, Integer> statistics = new HashMap<>();
        int postCount = postService.getPostCountByUser(userId);
        int commentCount = commentService.getCommentCountByUserId(userId);
        int likeCount = likeService.countLikesByUserId(userId);

        statistics.put("postCount", postCount);
        statistics.put("commentCount", commentCount);
        statistics.put("likeCount", likeCount);

        return statistics;
    }

    private String buildUserUpdateParams(UserDTO userDTO) {
        if (userDTO == null) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder("{");
        sb.append("username=").append(userDTO.getUsername());
        sb.append(", email=").append(userDTO.getEmail());
        sb.append(", bio=").append(userDTO.getBio());
        sb.append(", avatar=").append(userDTO.getAvatar());
        sb.append("}");
        return sb.toString();
    }
}
