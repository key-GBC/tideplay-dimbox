package com.dimbox.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dimbox.product.dto.CategoryRequestDto;
import com.dimbox.product.dto.CategoryTreeNodeDto;
import com.dimbox.product.entity.Category;
import com.dimbox.product.enums.ProductErrorCode;
import com.dimbox.product.mapper.CategoryMapper;
import com.dimbox.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分类服务实现。
 * <p>分类树通过「一次查询全部启用分类 + 内存按 parentId 分组递归」构建，避免多次查库。</p>
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryTreeNodeDto> tree() {
        return buildTree(true);
    }

    @Override
    public List<CategoryTreeNodeDto> adminTree() {
        return buildTree(false);
    }

    /**
     * 构建分类树；onlyEnabled 为 true 时只取启用分类。
     */
    private List<CategoryTreeNodeDto> buildTree(boolean onlyEnabled) {
        List<Category> all = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .eq(onlyEnabled, Category::getStatus, 1)
                        .orderByAsc(Category::getSort));
        // 按父分类 id 分组，便于递归构建子节点
        Map<Long, List<Category>> byParent = all.stream()
                .collect(Collectors.groupingBy(Category::getParentId));
        // 顶级分类的 parentId 为 0
        return buildChildren(0L, byParent);
    }

    @Override
    public Long add(CategoryRequestDto request) {
        Category category = new Category();
        category.setParentId(request.getParentId());
        category.setName(request.getName());
        category.setIcon(request.getIcon());
        category.setSort(request.getSort());
        // 未传状态时默认启用
        category.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        categoryMapper.insert(category);
        return category.getId();
    }

    @Override
    public void update(Long id, CategoryRequestDto request) {
        if (categoryMapper.selectById(id) == null) {
            throw ProductErrorCode.CATEGORY_NOT_FOUND.exception();
        }
        // 只更新非空字段（MyBatis-Plus 默认忽略 null 字段）
        Category category = new Category();
        category.setId(id);
        category.setParentId(request.getParentId());
        category.setName(request.getName());
        category.setIcon(request.getIcon());
        category.setSort(request.getSort());
        category.setStatus(request.getStatus());
        categoryMapper.updateById(category);
    }

    @Override
    public void delete(Long id) {
        if (categoryMapper.selectById(id) == null) {
            throw ProductErrorCode.CATEGORY_NOT_FOUND.exception();
        }
        // 存在子分类时不允许删除
        Long children = categoryMapper.selectCount(
                new LambdaQueryWrapper<Category>().eq(Category::getParentId, id));
        if (children != null && children > 0) {
            throw ProductErrorCode.CATEGORY_HAS_CHILDREN.exception();
        }
        // 逻辑删除
        categoryMapper.deleteById(id);
    }

    /**
     * 递归把某个父分类下的子分类组装成树节点。
     */
    private List<CategoryTreeNodeDto> buildChildren(Long parentId, Map<Long, List<Category>> byParent) {
        return byParent.getOrDefault(parentId, List.of()).stream().map(c ->
                CategoryTreeNodeDto.builder()
                        .id(c.getId())
                        .parentId(c.getParentId())
                        .name(c.getName())
                        .icon(c.getIcon())
                        .sort(c.getSort())
                        .status(c.getStatus())
                        .children(buildChildren(c.getId(), byParent))
                        .build()
        ).collect(Collectors.toList());
    }
}
