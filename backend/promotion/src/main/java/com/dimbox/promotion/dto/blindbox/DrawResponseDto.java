package com.dimbox.promotion.dto.blindbox;

import lombok.Data;

@Data
public class DrawResponseDto {

    private String orderNo;

    private String styleName;

    private String image;

    private Integer isHidden;
}
