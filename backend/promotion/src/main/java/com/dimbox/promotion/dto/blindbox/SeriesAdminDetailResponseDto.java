package com.dimbox.promotion.dto.blindbox;

import lombok.Data;

import java.util.List;

@Data
public class SeriesAdminDetailResponseDto {

    private Long id;

    private String name;

    private String description;

    private String coverImage;

    private Integer price;

    private Integer status;

    private List<BlindBoxItemAdminResponseDto> items;
}
