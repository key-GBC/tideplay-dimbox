package com.dimbox.promotion.dto.blindbox;

import lombok.Data;

@Data
public class BlindBoxItemAdminResponseDto {

    private Long id;

    private Long seriesId;

    private Long skuId;

    private String styleName;

    private String image;

    private Integer probability;

    private Integer isHidden;
}
