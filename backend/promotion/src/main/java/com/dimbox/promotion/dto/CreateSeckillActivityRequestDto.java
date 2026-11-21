package com.dimbox.promotion.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateSeckillActivityRequestDto {

    @NotNull
    @Min(1)
    private Long skuId;

    @NotNull
    @Min(1)
    private Integer seckillPrice;

    @NotNull
    @Min(1)
    private Integer stock;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
