package com.dimbox.promotion.dto.blindbox;

import lombok.Data;

import java.util.List;

@Data
public class SeriesDetailResponseDto {
    private Long id;

    private String name;

    private String description;

    private String coverImage;

    private Integer price;

    private List<BlindBoxItemResponseDto> items;

}
