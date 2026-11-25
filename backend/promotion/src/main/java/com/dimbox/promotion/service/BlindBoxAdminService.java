package com.dimbox.promotion.service;

import com.dimbox.promotion.dto.blindbox.ItemCreateRequestDto;
import com.dimbox.promotion.dto.blindbox.ItemUpdateRequestDto;
import com.dimbox.promotion.dto.blindbox.SeriesCreateRequestDto;
import com.dimbox.promotion.dto.blindbox.SeriesUpdateRequestDto;

public interface BlindBoxAdminService {

    Long createSeries(SeriesCreateRequestDto request);

    void updateSeries(Long id, SeriesUpdateRequestDto request);

    Long createItem(Long seriesId, ItemCreateRequestDto request);

    void updateItem(Long itemId, ItemUpdateRequestDto request);

    void deleteItem(Long itemId);
}
