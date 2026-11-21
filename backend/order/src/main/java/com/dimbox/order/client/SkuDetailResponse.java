package com.dimbox.order.client;

import lombok.Data;

/** 商品SKU详情响应 */
@Data
public class SkuDetailResponse {

    /** SKU id */
    private Long id;

    /** 商品 id */
    private Long productId;

    /** 款式/规格名 */
    private String skuName;

    /** 图片地址 */
    private String image;

    /** 价格，单位：分 */
    private Integer price;

    /** 库存数量 */
    private Integer stock;

    /** 状态：1 启用 / 0 禁用 */
    private Integer status;
}
