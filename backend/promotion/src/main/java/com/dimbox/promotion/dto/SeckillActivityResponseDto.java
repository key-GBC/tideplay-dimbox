package com.dimbox.promotion.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SeckillActivityResponseDto {

    private Long id;
    private Long skuId;
    private Integer seckillPrice;
    private Integer stock;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
}
