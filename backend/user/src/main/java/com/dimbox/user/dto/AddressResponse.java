package com.dimbox.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressResponse {

    private Long id;

    private String receiver;

    private String phone;

    private String province;

    private String city;

    private String district;

    private String detail;

    private Integer isDefault;
}
