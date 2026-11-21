package com.dimbox.order.dto;

import lombok.Data;

/**
 * 订单明细返回
 */
@Data
public class OrderItemResponseDto {

    /** 商品id */
    private Long productId;

    /** 商品sku id */
    private Long skuId;

    /** SKU 款式名快照 */
    private String skuName;

    /** 图片快照 */
    private String image;

    /** 成交单价，单位：分 */
    private Integer price;

    /** 购买数量 */
    private Integer quantity;
}
