package cn.edu.hnust.hnusteasyweibo.mapper;

import cn.edu.hnust.hnusteasyweibo.model.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 分类Mapper接口
 * 提供分类数据的数据库操作方法
 */
@Mapper
public interface CategoryMapper {
    /**
     * 查询所有启用的分类
     */
    @Select("SELECT * FROM categories WHERE enabled = true ORDER BY sort_order ASC")
    List<Category> findAllEnabled();

    /**
     * 根据ID查找分类
     */
    @Select("SELECT * FROM categories WHERE id = #{id}")
    Category findById(Long id);

    /**
     * 插入分类
     */
    @Insert("INSERT INTO categories (name, description, sort_order, enabled) VALUES (#{name}, #{description}, #{sortOrder}, #{enabled})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Category category);

    /**
     * 更新分类
     */
    @Update("UPDATE categories SET name = #{name}, description = #{description}, sort_order = #{sortOrder}, enabled = #{enabled} WHERE id = #{id}")
    void update(Category category);

    /**
     * 删除分类
     */
    @Delete("DELETE FROM categories WHERE id = #{id}")
    void delete(Long id);
}