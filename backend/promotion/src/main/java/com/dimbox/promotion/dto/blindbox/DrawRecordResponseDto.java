package com.dimbox.promotion.dto.blindbox;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DrawRecordResponseDto {

    private String orderNo;

    private Long seriesId;

    private Long itemId;

    private Long skuId;

    private Integer isHidden;

    private Integer price;

    private LocalDateTime createdAt;
}
