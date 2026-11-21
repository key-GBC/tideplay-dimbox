package com.dimbox.product.service;

import com.dimbox.product.dto.CategoryRequestDto;
import com.dimbox.product.dto.CategoryTreeNodeDto;

import java.util.List;

/**
 * 分类服务接口。
 * <p>提供分类树查询，以及分类的新增、修改、删除能力。</p>
 */
public interface CategoryService {

    /**
     * 查询启用的分类，并组装成树形结构。
     *
     * @return 分类树，顶级分类的 parentId 为 0
     */
    List<CategoryTreeNodeDto> tree();

    /**
     * 查询全部分类（含禁用），组装成树形结构，供管理端使用。
     *
     * @return 分类树
     */
    List<CategoryTreeNodeDto> adminTree();

    /**
     * 新增分类。
     *
     * @param request 分类信息
     * @return 新增分类的 id
     */
    Long add(CategoryRequestDto request);

    /**
     * 修改分类。
     *
     * @param id      分类 id
     * @param request 分类信息
     */
    void update(Long id, CategoryRequestDto request);

    /**
     * 删除分类（逻辑删除）。
     * <p>分类下存在子分类时不允许删除。</p>
     *
     * @param id 分类 id
     */
    void delete(Long id);
}
