package com.dimbox.promotion.service;

import com.dimbox.common.result.PageResult;
import com.dimbox.promotion.dto.blindbox.DrawRecordQueryDto;
import com.dimbox.promotion.dto.blindbox.DrawRecordResponseDto;
import com.dimbox.promotion.dto.blindbox.DrawResponseDto;
import com.dimbox.promotion.dto.blindbox.SeriesDetailResponseDto;
import com.dimbox.promotion.dto.blindbox.SeriesListQueryDto;
import com.dimbox.promotion.dto.blindbox.SeriesListResponseDto;

public interface BlindBoxService {

    PageResult<SeriesListResponseDto> pageSeries(SeriesListQueryDto query);

    SeriesDetailResponseDto getSeriesDetail(Long id);

    DrawResponseDto drawBlindbox(Long userId, Long seriesId);

    PageResult<DrawRecordResponseDto> getDrawRecord(Long userId, DrawRecordQueryDto query);
}
