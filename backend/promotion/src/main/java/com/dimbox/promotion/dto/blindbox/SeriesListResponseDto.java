package com.dimbox.promotion.dto.blindbox;

import lombok.Data;

@Data
public class SeriesListResponseDto {

    private Long id;

    private String name;

    private String description;

    private String coverImage;

    private Integer price;

    private Integer status;
}
