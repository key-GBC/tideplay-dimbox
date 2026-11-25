package com.dimbox.promotion.service;

import com.dimbox.common.result.PageResult;
import com.dimbox.promotion.dto.blindbox.DrawRecordQueryDto;
import com.dimbox.promotion.dto.blindbox.DrawRecordResponseDto;
import com.dimbox.promotion.dto.blindbox.DrawResponseDto;
import com.dimbox.promotion.dto.blindbox.SeriesDetailResponseDto;

public interface BlindBoxService {

    SeriesDetailResponseDto getSeriesDetail(Long id);

    DrawResponseDto drawBlindbox(Long userId, Long seriesId);

    PageResult<DrawRecordResponseDto> getDrawRecord(Long userId, DrawRecordQueryDto query);
}
