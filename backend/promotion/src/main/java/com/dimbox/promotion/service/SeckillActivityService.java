package com.dimbox.promotion.service;

import com.dimbox.common.result.PageResult;
import com.dimbox.promotion.dto.CreateSeckillActivityRequestDto;
import com.dimbox.promotion.dto.SeckillActivityResponseDto;
import com.dimbox.promotion.dto.SeckillActivityQueryRequestDto;

public interface SeckillActivityService {

    Long create(CreateSeckillActivityRequestDto dto);

    PageResult<SeckillActivityResponseDto> page(SeckillActivityQueryRequestDto query);

    void warmup(Long activityId);
}
