package com.dimbox.product.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 分类树节点（后端 → 前端，用户端分类树 / 管理端分类管理）。
 */
@Data
@Builder
public class CategoryTreeNodeDto {

    /** 分类 id */
    private Long id;

    /** 父分类 id，0 表示顶级分类 */
    private Long parentId;

    /** 分类名 */
    private String name;

    /** 分类图标地址 */
    private String icon;

    /** 排序值 */
    private Integer sort;

    /** 状态：1 启用 / 0 禁用 */
    private Integer status;

    /** 子分类节点 */
    private List<CategoryTreeNodeDto> children;
}
