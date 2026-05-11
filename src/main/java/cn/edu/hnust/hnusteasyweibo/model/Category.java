package cn.edu.hnust.hnusteasyweibo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分类/板块实体类
 * 表示帖子的分类板块
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