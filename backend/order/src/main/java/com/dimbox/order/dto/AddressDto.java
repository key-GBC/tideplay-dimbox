package com.dimbox.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 收货地址请求体，下单时作为订单快照提交。
 */
@Data
public class AddressDto {

    /** 收货人 */
    @NotBlank(message = "收货人不能为空")
    @Size(max = 32)
    private String receiver;

    /** 手机号 */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /** 省 */
    @NotBlank(message = "省不能为空")
    private String province;

    /** 市 */
    @NotBlank(message = "市不能为空")
    private String city;

    /** 区 */
    @NotBlank(message = "区不能为空")
    private String district;

    /** 详细地址 */
    @NotBlank(message = "详细地址不能为空")
    @Size(max = 255)
    private String detail;
}
