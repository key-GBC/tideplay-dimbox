package com.dimbox.order.enums;

import com.dimbox.common.exception.BizException;
import lombok.Getter;

@Getter
public enum OrderErrorCode {
    ORDER_NOT_FOUND(404, "订单不存在"),
    NO_PERMISSION(403, "无权操作该订单"),
    STOCK_NOT_ENOUGH(409, "库存不足"),
    SKU_NOT_FOUND(404, "商品款式不存在"),
    ORDER_STATUS_ERROR(409, "订单状态不允许该操作"),
    ORDER_DUPLICATE(409, "请勿重复提交订单"),
    PRODUCT_SERVICE_ERROR(500, "商品服务调用失败");

    private final int code;
    private final String message;

    OrderErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public BizException exception() {
        return new BizException(code, message);
    }
}
