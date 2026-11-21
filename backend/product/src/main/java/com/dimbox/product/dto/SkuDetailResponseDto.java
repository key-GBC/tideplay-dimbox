package com.dimbox.product.dto;

import lombok.Data;

/**
 * SKU 详情返回对象（内部服务调用：商品服务 → 订单服务）。
 * <p>相比 SkuResponseDto 多了 productId 与 status，供订单快照与校验使用。</p>
 */
@Data
public class SkuDetailResponseDto {

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
