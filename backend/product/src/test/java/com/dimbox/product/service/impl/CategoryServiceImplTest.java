package com.dimbox.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.dimbox.product.dto.CategoryTreeNodeDto;
import com.dimbox.product.entity.Category;
import com.dimbox.product.mapper.CategoryMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * CategoryServiceImpl 单元测试。
 * 重点验证「分类树」的组装逻辑：一次查询后按 parentId 递归构建。
 */
@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void tree_组装两级分类树() {
        // 顶级分类
        Category parent = new Category();
        parent.setId(1L);
        parent.setParentId(0L);
        parent.setName("潮玩手办");
        parent.setSort(1);
        parent.setStatus(1);

        // 子分类
        Category child = new Category();
        child.setId(2L);
        child.setParentId(1L);
        child.setName("盲盒");
        child.setSort(1);
        child.setStatus(1);

        // mock 查询结果：数据库返回「拍平」的两条记录
        when(categoryMapper.selectList(ArgumentMatchers.<Wrapper<Category>>any()))
                .thenReturn(List.of(parent, child));

        List<CategoryTreeNodeDto> tree = categoryService.tree();

        // 断言：顶级只有 1 个节点，且它的子节点只有 1 个，名称正确
        assertThat(tree).hasSize(1);
        assertThat(tree.get(0).getId()).isEqualTo(1L);
        assertThat(tree.get(0).getName()).isEqualTo("潮玩手办");
        assertThat(tree.get(0).getChildren()).hasSize(1);
        assertThat(tree.get(0).getChildren().get(0).getName()).isEqualTo("盲盒");
    }
}
