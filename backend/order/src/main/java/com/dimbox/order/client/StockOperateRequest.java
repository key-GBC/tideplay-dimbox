package com.dimbox.order.client;

import lombok.AllArgsConstructor;
import lombok.Data;

/** 商品库存操作请求 */
@Data
@AllArgsConstructor
public class StockOperateRequest {

    /** 商品sku id */
    private Long skuId;

    /** 购买数量 */
    private Integer quantity;

}
