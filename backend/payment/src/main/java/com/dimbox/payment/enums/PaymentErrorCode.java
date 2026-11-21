package com.dimbox.payment.enums;

import com.dimbox.common.exception.BizException;
import lombok.Getter;

/**
 * 支付服务错误码，取值与 HTTP 状态码一致，用 message 区分具体业务含义。
 */
@Getter
public enum PaymentErrorCode {

    PAYMENT_NOT_FOUND(404, "支付单不存在"),
    ORDER_NOT_FOUND(404, "订单不存在"),
    NO_PERMISSION(403, "无权操作该支付单"),
    ORDER_STATUS_ERROR(409, "订单状态不允许支付"),
    PAYMENT_ALREADY_PAID(409, "支付单已支付");

    /** HTTP 状态码 */
    private final int code;

    /** 用户可读的错误信息 */
    private final String message;

    PaymentErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /** 转成业务异常，由全局异常处理器统一转成 Result */
    public BizException exception() {
        return new BizException(code, message);
    }
}
