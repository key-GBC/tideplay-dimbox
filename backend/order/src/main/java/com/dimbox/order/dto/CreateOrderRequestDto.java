package com.dimbox.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 提交订单请求体。
 */
@Data
public class CreateOrderRequestDto {

    /** 客户端幂等键，重复提交返回原订单 */
    @NotBlank
    @Size(max = 64)
    private String requestId;

    /** 购买的 SKU id */
    @NotNull
    private Long skuId;

    /** 购买数量 */
    @NotNull
    @Min(1)
    @Max(99)
    private Integer quantity;

    /** 收货地址快照 */
    @Valid
    @NotNull
    private AddressDto address;

    /** 订单备注 */
    @Size(max = 255)
    private String remark;
}
