package com.dimbox.product.dto;

import lombok.Data;

import java.util.List;

/**
 * 商品详情返回对象（后端 → 用户端，含 SKU 列表）。
 */
@Data
public class ProductDetailResponseDto {

    /** 商品 id */
    private Long id;

    /** 商品名 */
    private String name;

    /** 副标题 */
    private String subTitle;

    /** 主图地址 */
    private String mainImage;

    /** 商品详情 */
    private String detail;

    /** 所属分类 id */
    private Long categoryId;

    /** 所属品牌 id */
    private Long brandId;

    /** SKU 列表 */
    private List<SkuResponseDto> skus;
}
