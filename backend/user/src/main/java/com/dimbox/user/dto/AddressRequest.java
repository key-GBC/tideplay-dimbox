package com.dimbox.user.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddressRequest {

    @NotBlank(message = "收货人不能为空")
    @Size(max = 32, message = "收货人最长 32 字")
    private String receiver;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @NotBlank(message = "省不能为空")
    private String province;

    @NotBlank(message = "市不能为空")
    private String city;

    @NotBlank(message = "区不能为空")
    private String district;

    @NotBlank(message = "详细地址不能为空")
    @Size(max = 255, message = "详细地址最长 255 字")
    private String detail;

    @Min(value = 0, message = "是否默认取值 0/1")
    @Max(value = 1, message = "是否默认取值 0/1")
    private Integer isDefault;
}
