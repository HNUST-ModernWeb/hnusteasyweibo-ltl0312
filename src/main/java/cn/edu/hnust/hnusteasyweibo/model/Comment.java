package cn.edu.hnust.hnusteasyweibo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private Long id;
    private Long postId;
    private Long userId;
    private String content;
    private Date createdAt;
    private Date updatedAt;
    private String username;
}
