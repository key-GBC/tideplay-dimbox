package com.dimbox.product.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 分类新增/修改请求参数（管理端 → 后端）。
 */
@Data
public class CategoryRequestDto {

    /** 父分类 id，0 表示顶级分类 */
    @NotNull(message = "父分类不能为空")
    private Long parentId;

    /** 分类名 */
    @NotBlank(message = "分类名不能为空")
    @Size(max = 64, message = "分类名最长64字")
    private String name;

    /** 分类图标地址 */
    @Size(max = 255, message = "图标地址过长")
    private String icon;

    /** 排序值，越小越靠前 */
    @NotNull(message = "排序不能为空")
    private Integer sort;

    /** 状态：1 启用 / 0 禁用 */
    @Min(value = 0, message = "状态不能小于0")
    @Max(value = 1, message = "状态不能大于1")
    private Integer status;
}
