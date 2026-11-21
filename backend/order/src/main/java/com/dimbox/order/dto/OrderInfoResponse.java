package com.dimbox.order.dto;

import lombok.Data;

/**
 * 订单信息（内部服务调用：订单服务 → 支付服务）。
 * <p>供支付服务校验订单归属、状态与金额，字段刻意精简。</p>
 */
@Data
public class OrderInfoResponse {

    private String orderNo;

    private Long userId;

    private Integer status;

    private Integer totalAmount;
}
