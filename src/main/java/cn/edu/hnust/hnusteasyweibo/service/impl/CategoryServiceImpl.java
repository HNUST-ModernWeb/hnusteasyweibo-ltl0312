package cn.edu.hnust.hnusteasyweibo.service.impl;

import cn.edu.hnust.hnusteasyweibo.exception.BusinessException;
import cn.edu.hnust.hnusteasyweibo.mapper.CategoryMapper;
import cn.edu.hnust.hnusteasyweibo.model.Category;
import cn.edu.hnust.hnusteasyweibo.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 分类服务实现类
 * 实现CategoryService接口中定义的分类相关业务逻辑
 *
 * @author hnust-easyweibo
 * @version 1.0
 * @see CategoryService
 */
@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<Category> getEnabledCategories() {
        return categoryMapper.findAllEnabled();
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        Category category = categoryMapper.findById(categoryId);
        if (category == null) {
            throw new BusinessException(404, "分类不存在");
        }
        return category;
    }

    @Override
    @Transactional
    public Category createCategory(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new BusinessException(400, "分类名称不能为空");
        }

        category.setEnabled(true);
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }

        categoryMapper.insert(category);
        return category;
    }

    @Override
    @Transactional
    public Category updateCategory(Long categoryId, Category category) {
        Category existingCategory = categoryMapper.findById(categoryId);
        if (existingCategory == null) {
            throw new BusinessException(404, "分类不存在");
        }

        if (category.getName() != null) {
            existingCategory.setName(category.getName());
        }
        if (category.getDescription() != null) {
            existingCategory.setDescription(category.getDescription());
        }
        if (category.getSortOrder() != null) {
            existingCategory.setSortOrder(category.getSortOrder());
        }
        if (category.getEnabled() != null) {
            existingCategory.setEnabled(category.getEnabled());
        }

        categoryMapper.update(existingCategory);
        return existingCategory;
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryMapper.findById(categoryId);
        if (category == null) {
            throw new BusinessException(404, "分类不存在");
        }

        categoryMapper.delete(categoryId);
    }
}