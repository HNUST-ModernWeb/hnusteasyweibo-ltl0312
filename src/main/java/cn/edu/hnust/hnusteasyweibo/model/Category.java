package cn.edu.hnust.hnusteasyweibo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分类/板块实体类
 * 用于表示帖子的分类板块
 *
 * <p>使用场景：帖子分类浏览、板块导航等业务场景</p>
 *
 * @author hnust-easyweibo
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    /**
     * 板块唯一标识ID
     */
    private Long id;

    /**
     * 板块名称
     */
    private String name;

    /**
     * 板块描述
     */
    private String description;

    /**
     * 排序序号
     */
    private Integer sortOrder;

    /**
     * 是否启用
     */
    private Boolean enabled;
}