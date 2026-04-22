package cn.edu.hnust.hnusteasyweibo.controller;

import cn.edu.hnust.hnusteasyweibo.constant.LogConstants;
import cn.edu.hnust.hnusteasyweibo.constant.ServerConstants;
import cn.edu.hnust.hnusteasyweibo.dto.CreateCommentDTO;
import cn.edu.hnust.hnusteasyweibo.dto.CreatePostDTO;
import cn.edu.hnust.hnusteasyweibo.dto.PostVO;
import cn.edu.hnust.hnusteasyweibo.dto.UpdatePostDTO;
import cn.edu.hnust.hnusteasyweibo.dto.ResponseDTO;
import cn.edu.hnust.hnusteasyweibo.model.Category;
import cn.edu.hnust.hnusteasyweibo.model.Comment;
import cn.edu.hnust.hnusteasyweibo.model.User;
import cn.edu.hnust.hnusteasyweibo.service.CategoryService;
import cn.edu.hnust.hnusteasyweibo.service.CommentService;
import cn.edu.hnust.hnusteasyweibo.service.LikeService;
import cn.edu.hnust.hnusteasyweibo.service.LogService;
import cn.edu.hnust.hnusteasyweibo.service.PostService;
import cn.edu.hnust.hnusteasyweibo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping(ServerConstants.API_BASE_PATH)
@Slf4j
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LogService logService;

    @Autowired
    private UserService userService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.avatar-dir}")
    private String avatarDir;

    @PostMapping(ServerConstants.POSTS_PATH)
    public ResponseDTO<?> createPost(@RequestBody CreatePostDTO createPostDTO, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseDTO.error(401, "未授权，请先登录");
        }
        PostVO post = postService.createPost(userId, createPostDTO);
        return ResponseDTO.success(post);
    }

    @PutMapping(ServerConstants.POST_BY_ID_PATH)
    public ResponseDTO<?> updatePost(@PathVariable("id") Long postId, @RequestBody UpdatePostDTO updatePostDTO, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseDTO.error(401, "未授权，请先登录");
        }
        try {
            PostVO post = postService.updatePostWithTimeCheck(postId, userId, updatePostDTO);
            return ResponseDTO.success(post);
        } catch (Exception e) {
            return ResponseDTO.error(400, e.getMessage());
        }
    }

    @DeleteMapping(ServerConstants.POST_BY_ID_PATH)
    public ResponseDTO<?> deletePost(@PathVariable("id") Long postId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseDTO.error(401, "未授权，请先登录");
        }
        postService.deletePost(postId, userId);
        return ResponseDTO.success("删除成功");
    }

    @GetMapping(ServerConstants.POST_BY_ID_PATH)
    public ResponseDTO<?> getPost(@PathVariable("id") Long postId) {
        PostVO post = postService.getPostById(postId);
        return ResponseDTO.success(post);
    }

    @GetMapping(ServerConstants.POSTS_PATH)
    public ResponseDTO<?> getPostList(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        List<PostVO> posts = postService.getPostList(page, size);
        int total = postService.getPostCount();
        return ResponseDTO.success(new PageResult<>(posts, total));
    }

    @GetMapping(ServerConstants.POSTS_BY_CATEGORY_PATH)
    public ResponseDTO<?> getPostListByCategory(@PathVariable Long categoryId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        List<PostVO> posts = postService.getPostListByCategory(categoryId, page, size);
        int total = postService.getPostCountByCategory(categoryId);
        return ResponseDTO.success(new PageResult<>(posts, total));
    }

    @GetMapping(ServerConstants.POSTS_BY_USER_PATH)
    public ResponseDTO<?> getPostListByUser(@PathVariable Long userId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        List<PostVO> posts = postService.getPostListByUser(userId, page, size);
        int total = postService.getPostCountByUser(userId);
        return ResponseDTO.success(new PageResult<>(posts, total));
    }

    @GetMapping(ServerConstants.CATEGORIES_PATH)
    public ResponseDTO<?> getCategories() {
        List<Category> categories = categoryService.getEnabledCategories();
        return ResponseDTO.success(categories);
    }

    @GetMapping("/posts/user")
    public ResponseDTO<?> getCurrentUserPosts(HttpServletRequest request, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseDTO.error(401, "未授权，请先登录");
        }
        List<PostVO> posts = postService.getPostListByUser(userId, page, size);
        int total = postService.getPostCountByUser(userId);
        return ResponseDTO.success(new PageResult<>(posts, total));
    }

    @PostMapping("/posts/{id}/like")
    public ResponseDTO<?> likePost(@PathVariable("id") Long postId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseDTO.error(401, "未授权，请先登录");
        }
        int likeCount = likeService.likePost(postId, userId);
        postService.syncLikesCount(postId);
        return ResponseDTO.success(likeCount);
    }

    @PostMapping("/posts/{id}/unlike")
    public ResponseDTO<?> unlikePost(@PathVariable("id") Long postId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseDTO.error(401, "未授权，请先登录");
        }
        int likeCount = likeService.unlikePost(postId, userId);
        postService.syncLikesCount(postId);
        return ResponseDTO.success(likeCount);
    }

    @GetMapping("/posts/{id}/liked")
    public ResponseDTO<?> isLiked(@PathVariable("id") Long postId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseDTO.success(false);
        }
        boolean isLiked = likeService.isLiked(postId, userId);
        return ResponseDTO.success(isLiked);
    }

    @GetMapping("/posts/{id}/like-count")
    public ResponseDTO<?> getLikeCount(@PathVariable("id") Long postId) {
        int likeCount = likeService.getLikeCount(postId);
        return ResponseDTO.success(likeCount);
    }

    @PostMapping("/posts/{id}/comments")
    public ResponseDTO<?> createComment(@PathVariable("id") Long postId, @RequestBody CreateCommentDTO commentDTO, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseDTO.error(401, "未授权，请先登录");
        }
        if (commentDTO == null || commentDTO.getContent() == null || commentDTO.getContent().trim().isEmpty()) {
            return ResponseDTO.error(400, "评论内容不能为空");
        }
        Comment comment = commentService.createComment(postId, userId, commentDTO.getContent());
        postService.syncCommentsCount(postId);
        return ResponseDTO.success(comment);
    }

    @GetMapping("/posts/{id}/comments")
    public ResponseDTO<?> getComments(@PathVariable("id") Long postId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        List<Comment> comments = commentService.getCommentsByPostId(postId, page, size);
        int total = commentService.getCommentCountByPostId(postId);
        return ResponseDTO.success(new PageResult<>(comments, total));
    }

    @DeleteMapping("/comments/{id}")
    public ResponseDTO<?> deleteComment(@PathVariable("id") Long commentId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseDTO.error(401, "未授权，请先登录");
        }
        boolean success = commentService.deleteComment(commentId, userId);
        if (success) {
            return ResponseDTO.success("删除成功");
        } else {
            return ResponseDTO.error(403, "删除失败，无权限或评论不存在");
        }
    }

    @GetMapping("/posts/{id}/comment-count")
    public ResponseDTO<?> getCommentCount(@PathVariable("id") Long postId) {
        int commentCount = commentService.getCommentCountByPostId(postId);
        return ResponseDTO.success(commentCount);
    }

    @GetMapping("/posts/search")
    public ResponseDTO<?> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseDTO.error(400, "搜索关键词不能为空");
        }
        List<PostVO> posts = postService.searchPosts(keyword.trim(), page, size);
        int total = postService.getSearchPostCount(keyword.trim());
        return ResponseDTO.success(new PageResult<>(posts, total));
    }

    @GetMapping("/posts/{id}/editable")
    public ResponseDTO<?> checkEditable(@PathVariable("id") Long postId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseDTO.error(401, "未授权，请先登录");
        }
        try {
            boolean editable = postService.isPostEditable(postId, userId);
            return ResponseDTO.success(editable);
        } catch (Exception e) {
            return ResponseDTO.error(400, e.getMessage());
        }
    }

    @GetMapping("/files/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String username = resolveUsername(userId);
        try {
            String projectRoot = System.getProperty("user.dir");

            Path filePath = Paths.get(projectRoot, uploadDir.replace("./", ""), filename);
            File file = filePath.toFile();

            if (!file.exists()) {
                filePath = Paths.get(projectRoot, avatarDir.replace("./", ""), filename);
                file = filePath.toFile();
                if (!file.exists()) {
                    logService.logOperation(
                            userId,
                            username,
                            LogConstants.OPERATION_TYPE_DOWNLOAD,
                            LogConstants.OPERATION_MODULE_FILE,
                            "下载文件失败：文件不存在",
                            "GET",
                            "/api/v1/files/download/" + filename,
                            404,
                            LogConstants.REQUEST_STATUS_FAILED,
                            "filename=" + filename,
                            request,
                            0.0
                    );
                    return ResponseEntity.notFound().build();
                }
            }

            FileSystemResource resource = new FileSystemResource(file);
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8);
            logService.logOperation(
                    userId,
                    username,
                    LogConstants.OPERATION_TYPE_DOWNLOAD,
                    LogConstants.OPERATION_MODULE_FILE,
                    "下载文件成功：" + filename,
                    "GET",
                    "/api/v1/files/download/" + filename,
                    200,
                    LogConstants.REQUEST_STATUS_SUCCESS,
                    "filename=" + filename,
                    request,
                    0.0
            );
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            logService.logOperation(
                    userId,
                    username,
                    LogConstants.OPERATION_TYPE_DOWNLOAD,
                    LogConstants.OPERATION_MODULE_FILE,
                    "下载文件异常：" + e.getMessage(),
                    "GET",
                    "/api/v1/files/download/" + filename,
                    500,
                    LogConstants.REQUEST_STATUS_ERROR,
                    "filename=" + filename,
                    request,
                    0.0
            );
            return ResponseEntity.internalServerError().build();
        }
    }

    private String resolveUsername(Long userId) {
        if (userId == null) {
            return "anonymous";
        }
        try {
            User user = userService.getUserById(userId);
            return user.getUsername();
        } catch (Exception e) {
            return String.valueOf(userId);
        }
    }

    private static class PageResult<T> {
        private List<T> items;
        private int total;

        public PageResult(List<T> items, int total) {
            this.items = items;
            this.total = total;
        }

        public List<T> getItems() {
            return items;
        }

        public int getTotal() {
            return total;
        }
    }
}
