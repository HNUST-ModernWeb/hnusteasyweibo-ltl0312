package cn.edu.hnust.hnusteasyweibo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 点赞实体类
 * 映射数据库中likes表，表示用户对帖子的点赞记录
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Like {
    /**
     * 点赞记录唯一标识ID
     */
    private long id;

    /**
     * 帖子ID
     * 被点赞的帖子ID
     */
    private long postId;

    /**
     * 用户ID
     * 发起点赞操作用户ID
     */
    private long userId;

    /**
     * 创建时间
     * 点赞操作发生的时间
     */
    private Date createdAt;
}
