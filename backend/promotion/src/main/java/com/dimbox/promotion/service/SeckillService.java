package com.dimbox.promotion.service;

import com.dimbox.common.result.PageResult;
import com.dimbox.promotion.dto.SeckillOrderResponseDto;
import com.dimbox.promotion.dto.SeckillRecordQueryDto;

public interface SeckillService {

    String buy(Long userId, Long activityId);

    PageResult<SeckillOrderResponseDto> pageRecord(Long userId, SeckillRecordQueryDto query);
}
