package com.dimbox.payment.client;

import lombok.Data;

/**
 * order 返回的订单信息（Feign 响应）。
 * <p>字段与 order 服务的 OrderInfoResponse 对齐，服务间靠字段约定对接，不共享代码。</p>
 */
@Data
public class OrderInfoResponse {

    /** 订单号 */
    private String orderNo;

    /** 下单用户 id */
    private Long userId;

    /** 订单状态：10 待支付 / 20 已支付 / 50 已取消 */
    private Integer status;

    /** 订单总额，单位：分 */
    private Integer totalAmount;

}
