package cn.edu.hnust.hnusteasyweibo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostVO {
    private Long id;
    private Long userId;
    private String username;
    private String avatar;
    private Long categoryId;
    private String categoryName;
    private String content;
    private String image;
    private int viewCount;
    private int likesCount;
    private int commentsCount;
    private Date createdAt;
    private Date updatedAt;
    private Boolean liked;
}
