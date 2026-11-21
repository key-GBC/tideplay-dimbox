package com.dimbox.order.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单详情返回体，含收货地址快照与明细列表。
 */
@Data
public class OrderDetailResponseDto {

    /** 订单id */
    private Long id;

    /** 订单编号 */
    private String orderNo;

    /** 订单状态 */
    private Integer status;

    /** 订单总金额，单位：分 */
    private Integer totalAmount;

    /** 收货人 */
    private String receiver;

    /** 收货人手机号 */
    private String phone;

    /** 省份 */
    private String province;

    /** 城市 */
    private String city;

    /** 区 */
    private String district;

    /** 详细地址 */
    private String detail;

    /** 订单备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 订单商品列表 */
    private List<OrderItemResponseDto> items;

}
