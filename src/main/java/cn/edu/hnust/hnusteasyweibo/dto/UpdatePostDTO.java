package cn.edu.hnust.hnusteasyweibo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新帖子请求DTO
 * 用于接收用户更新帖子的请求数据
 *
 * @author hnust-easyweibo
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePostDTO {
    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 帖子内容
     * 长度限制10-2000字
     */
    private String content;

    /**
     * 图片地址
     * 多个图片用逗号分隔
     */
    private String image;
}