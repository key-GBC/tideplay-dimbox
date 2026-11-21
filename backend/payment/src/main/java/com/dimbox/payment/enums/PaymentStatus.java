package com.dimbox.payment.enums;

import lombok.Getter;

/**
 * 支付单状态。
 */
@Getter
public enum PaymentStatus {

    PENDING(10, "待支付"),
    PAID(20, "已支付");

    /** 状态码，与 t_payment.status 对齐 */
    private final int code;

    /** 状态描述 */
    private final String desc;

    PaymentStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
