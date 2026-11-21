package com.dimbox.product.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 商品新增/修改请求参数（管理端 → 后端，含 SKU 列表）。
 */
@Data
public class ProductSaveRequestDto {

    /** 所属分类 id */
    @NotNull(message = "分类不能为空")
    private Long categoryId;

    /** 所属品牌 id，可为空 */
    private Long brandId;

    /** 商品名 */
    @NotBlank(message = "商品名不能为空")
    @Size(max = 128, message = "商品名最长128字")
    private String name;

    /** 副标题 */
    @Size(max = 255, message = "副标题最长255字")
    private String subTitle;

    /** 主图地址 */
    @Size(max = 255, message = "主图片地址过长")
    private String mainImage;

    /** 商品详情 */
    private String detail;

    /** SKU 列表，至少一个，级联校验每个 SKU */
    @NotEmpty(message = "至少需要一个款式")
    @Valid
    private List<SkuRequestDto> skus;
}
