package cn.edu.hnust.hnusteasyweibo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    private Long id;
    private Long userId;
    private Long categoryId;
    private String content;
    private String image;
    private Integer viewCount;
    private Integer likesCount;
    private Integer commentsCount;
    private Date createdAt;
    private Date updatedAt;
    private String categoryName;
}
