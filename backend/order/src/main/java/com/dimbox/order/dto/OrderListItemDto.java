package com.dimbox.order.dto;


import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单列表项返回体。
 */
@Data
public class OrderListItemDto {

    /** 订单id */
    private Long id;

    /** 订单编号 */
    private String orderNo;

    /** 订单状态 */
    private Integer status;

    /** 订单总金额，单位：分 */
    private Integer totalAmount;

    /** 创建时间 */
    private LocalDateTime createdAt;
}
