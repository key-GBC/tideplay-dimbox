package com.dimbox.product.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 品牌新增/修改请求参数（管理端 → 后端）。
 */
@Data
public class BrandRequestDto {

    /** 品牌名 */
    @NotBlank(message = "品牌名不能为空")
    @Size(max = 64, message = "品牌名最长64字")
    private String name;

    /** 品牌 Logo 地址 */
    @Size(max = 255, message = "品牌logo最长255字")
    private String logo;

    /** 品牌描述 */
    @Size(max = 500, message = "品牌描述最长500字")
    private String description;

    /** 状态：1 启用 / 0 禁用 */
    @Min(value = 0, message = "状态不能小于0")
    @Max(value = 1, message = "状态不能大于1")
    private Integer status;
}
