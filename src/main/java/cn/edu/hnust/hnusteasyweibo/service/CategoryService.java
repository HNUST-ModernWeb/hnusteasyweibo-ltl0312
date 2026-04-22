package cn.edu.hnust.hnusteasyweibo.service;

import cn.edu.hnust.hnusteasyweibo.model.Category;

import java.util.List;

/**
 * 分类服务接口
 * 定义分类相关的业务操作方法
 *
 * <p>主要功能：
 * <ul>
 *   <li>获取分类列表</li>
 *   <li>管理分类（后台）</li>
 * </ul>
 * </p>
 *
 * @author hnust-easyweibo
 * @version 1.0
 */
public interface CategoryService {
    /**
     * 获取所有启用的分类
     *
     * @return 分类列表
     */
    List<Category> getEnabledCategories();

    /**
     * 根据ID获取分类
     *
     * @param categoryId 分类ID
     * @return 分类对象
     */
    Category getCategoryById(Long categoryId);

    /**
     * 创建分类（后台管理）
     *
     * @param category 分类对象
     * @return 创建的分类
     */
    Category createCategory(Category category);

    /**
     * 更新分类（后台管理）
     *
     * @param categoryId 分类ID
     * @param category 分类对象
     * @return 更新后的分类
     */
    Category updateCategory(Long categoryId, Category category);

    /**
     * 删除分类（后台管理）
     *
     * @param categoryId 分类ID
     */
    void deleteCategory(Long categoryId);
}