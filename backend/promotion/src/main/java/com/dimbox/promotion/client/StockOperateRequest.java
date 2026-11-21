package com.dimbox.promotion.client;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StockOperateRequest {

    private Long skuId;

    private Integer quantity;
}
