package com.dimbox.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建支付单请求。
 */
@Data
public class CreatePaymentRequestDto {

    /** 要支付的订单号 */
    @NotBlank(message = "订单号不能为空")
    private String orderNo;
}
