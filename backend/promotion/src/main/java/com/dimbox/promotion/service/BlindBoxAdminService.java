package com.dimbox.promotion.service;

import com.dimbox.promotion.dto.blindbox.ItemCreateRequestDto;
import com.dimbox.promotion.dto.blindbox.ItemUpdateRequestDto;
import com.dimbox.promotion.dto.blindbox.SeriesCreateRequestDto;
import com.dimbox.promotion.dto.blindbox.SeriesAdminDetailResponseDto;
import com.dimbox.promotion.dto.blindbox.SeriesListQueryDto;
import com.dimbox.promotion.dto.blindbox.SeriesListResponseDto;
import com.dimbox.promotion.dto.blindbox.SeriesUpdateRequestDto;
import com.dimbox.common.result.PageResult;

public interface BlindBoxAdminService {

    PageResult<SeriesListResponseDto> adminPageSeries(SeriesListQueryDto query);

    SeriesAdminDetailResponseDto adminGetSeriesDetail(Long id);

    Long createSeries(SeriesCreateRequestDto request);

    void updateSeries(Long id, SeriesUpdateRequestDto request);

    Long createItem(Long seriesId, ItemCreateRequestDto request);

    void updateItem(Long itemId, ItemUpdateRequestDto request);

    void deleteItem(Long itemId);
}
