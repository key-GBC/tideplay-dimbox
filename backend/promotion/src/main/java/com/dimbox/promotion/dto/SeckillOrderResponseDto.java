package com.dimbox.promotion.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SeckillOrderResponseDto {

    private Long id;

    private Long activityId;

    private Long skuId;

    private String orderNo;

    private Integer status;

    private LocalDateTime createAt;
}
