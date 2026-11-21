package com.dimbox.order.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {

    PENDING_PAYMENT(10, "待支付"),
    PAID(20, "已支付"),
    CANCELLED(50, "已取消");

    private final int code;
    private final String desc;

    OrderStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }


}
