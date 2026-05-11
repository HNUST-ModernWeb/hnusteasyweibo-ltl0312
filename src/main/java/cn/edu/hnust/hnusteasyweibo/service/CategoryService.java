package cn.edu.hnust.hnusteasyweibo.service;

import cn.edu.hnust.hnusteasyweibo.model.Category;

import java.util.List;

/**
 * 分类服务接口
 * 定义分类相关的业务操作方法
 */
public interface CategoryService {
    /**
     * 获取所有启用的分类
     */
    List<Category> getEnabledCategories();

    /**
     * 根据ID获取分类
     */
    Category getCategoryById(Long categoryId);

    /**
     * 创建分类（后台管理）
     */
    Category createCategory(Category category);

    /**
     * 更新分类（后台管理）
     */
    Category updateCategory(Long categoryId, Category category);

    /**
     * 删除分类（后台管理）
     */
    void deleteCategory(Long categoryId);
}