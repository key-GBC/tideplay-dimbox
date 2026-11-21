package com.dimbox.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 支付回调请求。
 */
@Data
public class PaymentCallbackRequestDto {

    /** 支付单号 */
    @NotBlank(message = "支付单号不能为空")
    private String paymentNo;

    /** 支付渠道 */
    private String channel;
}
