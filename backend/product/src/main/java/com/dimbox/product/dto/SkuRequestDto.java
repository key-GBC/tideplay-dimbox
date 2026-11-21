package com.dimbox.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * SKU 请求参数（管理端 → 后端，内嵌在商品保存请求中）。
 */
@Data
public class SkuRequestDto {

    /** 款式/规格名 */
    @NotBlank(message = "款式名不能为空")
    @Size(max = 128, message = "款式名最长128字")
    private String skuName;

    /** SKU 图片地址 */
    @Size(max = 255, message = "图片地址过长")
    private String image;

    /** 价格，单位：分 */
    @NotNull(message = "价格不能为空")
    @Min(value = 0, message = "价格不能为负")
    private Integer price;

    /** 库存数量 */
    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能为负")
    private Integer stock;
}
