package cn.edu.hnust.hnusteasyweibo.service.impl;

import cn.edu.hnust.hnusteasyweibo.dto.CreatePostDTO;
import cn.edu.hnust.hnusteasyweibo.dto.PostVO;
import cn.edu.hnust.hnusteasyweibo.dto.UpdatePostDTO;
import cn.edu.hnust.hnusteasyweibo.exception.BusinessException;
import cn.edu.hnust.hnusteasyweibo.mapper.PostMapper;
import cn.edu.hnust.hnusteasyweibo.mapper.UserMapper;
import cn.edu.hnust.hnusteasyweibo.model.Post;
import cn.edu.hnust.hnusteasyweibo.model.User;
import cn.edu.hnust.hnusteasyweibo.service.LogService;
import cn.edu.hnust.hnusteasyweibo.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LogService logService;

    private static final int MIN_CONTENT_LENGTH = 10;
    private static final int MAX_CONTENT_LENGTH = 2000;
    private static final long EDIT_TIME_LIMIT_MS = 10 * 60 * 1000;

    @Override
    @Transactional
    public PostVO createPost(Long userId, CreatePostDTO createPostDTO) {
        validateContent(createPostDTO.getContent());

        Post post = new Post();
        post.setUserId(userId);
        post.setCategoryId(createPostDTO.getCategoryId());
        post.setContent(createPostDTO.getContent());
        post.setImage(createPostDTO.getImage());
        post.setViewCount(0);
        post.setLikesCount(0);
        post.setCommentsCount(0);
        post.setCreatedAt(new Date());
        post.setUpdatedAt(new Date());

        postMapper.insert(post);

        User user = userMapper.findById(userId);
        if (user != null) {
            logService.logOperation(
                userId,
                user.getUsername(),
                "POST_CREATE",
                "POST",
                "发布帖子: " + createPostDTO.getContent().substring(0, Math.min(50, createPostDTO.getContent().length())),
                "POST",
                "/api/v1/posts",
                200,
                "SUCCESS",
                createPostDTO.toString(),
                null,
                0
            );
        }

        return getPostVO(post);
    }

    @Override
    @Transactional
    public PostVO updatePost(Long postId, Long userId, UpdatePostDTO updatePostDTO) {
        Post post = postMapper.findById(postId);
        if (post == null) {
            throw new BusinessException(404, "帖子不存在");
        }

        if (post.getUserId() != userId) {
            throw new BusinessException(403, "无权限修改此帖子");
        }

        if (updatePostDTO.getContent() != null) {
            validateContent(updatePostDTO.getContent());
            post.setContent(updatePostDTO.getContent());
        }

        if (updatePostDTO.getCategoryId() != null) {
            post.setCategoryId(updatePostDTO.getCategoryId());
        }

        if (updatePostDTO.getImage() != null) {
            post.setImage(updatePostDTO.getImage());
        }

        post.setUpdatedAt(new Date());
        postMapper.update(post);

        User user = userMapper.findById(userId);
        if (user != null) {
            logService.logOperation(
                userId,
                user.getUsername(),
                "POST_UPDATE",
                "POST",
                "编辑帖子 ID: " + postId,
                "PUT",
                "/api/v1/posts/" + postId,
                200,
                "SUCCESS",
                updatePostDTO.toString(),
                null,
                0
            );
        }

        return getPostVO(post);
    }

    @Override
    @Transactional
    public PostVO updatePostWithTimeCheck(Long postId, Long userId, UpdatePostDTO updatePostDTO) {
        Post post = postMapper.findById(postId);
        if (post == null) {
            throw new BusinessException(404, "帖子不存在");
        }

        if (post.getUserId() != userId) {
            throw new BusinessException(403, "无权限修改此帖子");
        }

        long timeDiff = System.currentTimeMillis() - post.getCreatedAt().getTime();
        if (timeDiff > EDIT_TIME_LIMIT_MS) {
            throw new BusinessException(400, "帖子创建已超过10分钟，无法编辑");
        }

        return updatePost(postId, userId, updatePostDTO);
    }

    @Override
    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postMapper.findById(postId);
        if (post == null) {
            throw new BusinessException(404, "帖子不存在");
        }

        if (post.getUserId() != userId) {
            throw new BusinessException(403, "无权限删除此帖子");
        }

        postMapper.delete(postId);

        User user = userMapper.findById(userId);
        if (user != null) {
            logService.logOperation(
                userId,
                user.getUsername(),
                "POST_DELETE",
                "POST",
                "删除帖子 ID: " + postId,
                "DELETE",
                "/api/v1/posts/" + postId,
                200,
                "SUCCESS",
                "{}",
                null,
                0
            );
        }
    }

    @Override
    public PostVO getPostById(Long postId) {
        Post post = postMapper.findById(postId);
        if (post == null) {
            throw new BusinessException(404, "帖子不存在");
        }
        return getPostVO(post);
    }

    @Override
    public List<PostVO> getPostList(int page, int size) {
        int offset = (page - 1) * size;
        List<Post> posts = postMapper.findAll(offset, size);
        return posts.stream().map(this::getPostVO).collect(Collectors.toList());
    }

    @Override
    public List<PostVO> getPostListByCategory(Long categoryId, int page, int size) {
        int offset = (page - 1) * size;
        List<Post> posts = postMapper.findByCategoryId(categoryId, offset, size);
        return posts.stream().map(this::getPostVO).collect(Collectors.toList());
    }

    @Override
    public List<PostVO> getPostListByUser(Long userId, int page, int size) {
        int offset = (page - 1) * size;
        List<Post> posts = postMapper.findByUserId(userId, offset, size);
        return posts.stream().map(this::getPostVO).collect(Collectors.toList());
    }

    @Override
    public int getPostCount() {
        return postMapper.countAll();
    }

    @Override
    public int getPostCountByCategory(Long categoryId) {
        return postMapper.countByCategoryId(categoryId);
    }

    @Override
    public int getPostCountByUser(Long userId) {
        return postMapper.countByUserId(userId);
    }

    @Override
    public void incrementViewCount(Long postId) {
        try {
            postMapper.incrementViewCount(postId);
        } catch (Exception e) {
            log.warn("Failed to increment view count for post {}: {}", postId, e.getMessage());
        }
    }

    @Override
    public void syncLikesCount(Long postId) {
        try {
            int count = postMapper.countLikesForPost(postId);
            postMapper.updateLikesCount(postId, count);
        } catch (Exception e) {
            log.warn("Failed to sync likes count for post {}: {}", postId, e.getMessage());
        }
    }

    @Override
    public void syncCommentsCount(Long postId) {
        try {
            int count = postMapper.countCommentsForPost(postId);
            postMapper.updateCommentsCount(postId, count);
        } catch (Exception e) {
            log.warn("Failed to sync comments count for post {}: {}", postId, e.getMessage());
        }
    }

    @Override
    public List<PostVO> searchPosts(String keyword, int page, int size) {
        int offset = (page - 1) * size;
        String likeKeyword = "%" + keyword + "%";
        List<Post> posts = postMapper.searchPosts(likeKeyword, offset, size);
        return posts.stream().map(this::getPostVO).collect(Collectors.toList());
    }

    @Override
    public int getSearchPostCount(String keyword) {
        String likeKeyword = "%" + keyword + "%";
        return postMapper.countSearchPosts(likeKeyword);
    }

    @Override
    public boolean isPostEditable(Long postId, Long userId) {
        Post post = postMapper.findById(postId);
        if (post == null) {
            throw new BusinessException(404, "帖子不存在");
        }
        if (post.getUserId() != userId) {
            return false;
        }
        long timeDiff = System.currentTimeMillis() - post.getCreatedAt().getTime();
        return timeDiff <= EDIT_TIME_LIMIT_MS;
    }

    private PostVO getPostVO(Post post) {
        PostVO vo = new PostVO();
        vo.setId(post.getId());
        vo.setUserId(post.getUserId());
        vo.setCategoryId(post.getCategoryId());
        vo.setContent(post.getContent());
        vo.setImage(post.getImage());
        vo.setViewCount(post.getViewCount() != null ? post.getViewCount() : 0);
        vo.setLikesCount(post.getLikesCount() != null ? post.getLikesCount() : 0);
        vo.setCommentsCount(post.getCommentsCount() != null ? post.getCommentsCount() : 0);
        vo.setCreatedAt(post.getCreatedAt());
        vo.setUpdatedAt(post.getUpdatedAt());

        User user = userMapper.findById(post.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setAvatar(user.getAvatar());
        }

        if (post.getCategoryName() != null) {
            vo.setCategoryName(post.getCategoryName());
        }

        return vo;
    }

    private void validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new BusinessException(400, "帖子内容不能为空");
        }

        int length = content.trim().length();
        if (length < MIN_CONTENT_LENGTH) {
            throw new BusinessException(400, "帖子内容不能少于" + MIN_CONTENT_LENGTH + "字");
        }

        if (length > MAX_CONTENT_LENGTH) {
            throw new BusinessException(400, "帖子内容不能超过" + MAX_CONTENT_LENGTH + "字");
        }
    }
}
