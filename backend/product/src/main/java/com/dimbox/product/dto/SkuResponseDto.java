package com.dimbox.product.dto;

import lombok.Data;

/**
 * SKU 信息返回对象（后端 → 用户端，内嵌在商品详情中）。
 */
@Data
public class SkuResponseDto {

    /** SKU id */
    private Long id;

    /** 款式/规格名 */
    private String skuName;

    /** SKU 图片地址 */
    private String image;

    /** 价格，单位：分 */
    private Integer price;

    /** 库存数量 */
    private Integer stock;

    /** 状态：1 上架 / 0 下架 */
    private Integer status;
}
