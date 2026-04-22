package cn.edu.hnust.hnusteasyweibo.service;

import cn.edu.hnust.hnusteasyweibo.dto.CreatePostDTO;
import cn.edu.hnust.hnusteasyweibo.dto.PostVO;
import cn.edu.hnust.hnusteasyweibo.dto.UpdatePostDTO;

import java.util.List;

public interface PostService {
    PostVO createPost(Long userId, CreatePostDTO createPostDTO);
    PostVO updatePost(Long postId, Long userId, UpdatePostDTO updatePostDTO);
    PostVO updatePostWithTimeCheck(Long postId, Long userId, UpdatePostDTO updatePostDTO);
    void deletePost(Long postId, Long userId);
    PostVO getPostById(Long postId);
    List<PostVO> getPostList(int page, int size);
    List<PostVO> getPostListByCategory(Long categoryId, int page, int size);
    List<PostVO> getPostListByUser(Long userId, int page, int size);
    int getPostCount();
    int getPostCountByCategory(Long categoryId);
    int getPostCountByUser(Long userId);
    void incrementViewCount(Long postId);
    void syncLikesCount(Long postId);
    void syncCommentsCount(Long postId);
    List<PostVO> searchPosts(String keyword, int page, int size);
    int getSearchPostCount(String keyword);
    boolean isPostEditable(Long postId, Long userId);
}
