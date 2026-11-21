package com.dimbox.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 库存扣减请求参数（内部服务调用：订单模块 → 商品服务）。
 */
@Data
public class StockDeductRequestDto {

    /** 要扣减的 SKU id */
    @NotNull(message = "款式不能为空")
    private Long skuId;

    /** 扣减数量，至少 1 */
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量不能小于1")
    private Integer quantity;
}
