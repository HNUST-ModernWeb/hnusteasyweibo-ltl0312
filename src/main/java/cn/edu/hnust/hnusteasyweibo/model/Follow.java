package cn.edu.hnust.hnusteasyweibo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 关注实体类
 * 表示用户之间的关注关系
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Follow {
    /**
     * 关注关系唯一标识ID
     */
    private long id;

    /**
     * 粉丝用户ID
     * 发起关注操作的用户ID
     */
    private long followerId;

    /**
     * 被关注用户ID
     * 被关注的用户ID
     */
    private long followingId;

    /**
     * 创建时间
     * 关注操作发生的时间
     */
    private Date createdAt;
}
