package com.dimbox.payment.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 支付单响应。
 */
@Data
public class PaymentResponseDto {

    /** 支付单号 */
    private String paymentNo;

    /** 关联订单号 */
    private String orderNo;

    /** 支付金额，单位：分 */
    private Integer amount;

    /** 支付渠道 */
    private String channel;

    /** 状态：10 待支付 / 20 已支付 */
    private Integer status;

    /** 支付时间 */
    private LocalDateTime paidAt;

}
