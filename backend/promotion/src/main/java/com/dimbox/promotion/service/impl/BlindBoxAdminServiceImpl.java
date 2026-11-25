package com.dimbox.promotion.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dimbox.promotion.dto.blindbox.ItemCreateRequestDto;
import com.dimbox.promotion.dto.blindbox.ItemUpdateRequestDto;
import com.dimbox.promotion.dto.blindbox.SeriesCreateRequestDto;
import com.dimbox.promotion.dto.blindbox.SeriesUpdateRequestDto;
import com.dimbox.promotion.entity.BlindBoxItem;
import com.dimbox.promotion.entity.BlindBoxSeries;
import com.dimbox.promotion.enums.PromotionErrorCode;
import com.dimbox.promotion.mapper.BlindBoxItemMapper;
import com.dimbox.promotion.mapper.BlindBoxSeriesMapper;
import com.dimbox.promotion.service.BlindBoxAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlindBoxAdminServiceImpl implements BlindBoxAdminService {

    private static final int PROBABILITY_TOTAL = 10000;

    private final BlindBoxSeriesMapper seriesMapper;
    private final BlindBoxItemMapper itemMapper;

    @Override
    public Long createSeries(SeriesCreateRequestDto request) {
        BlindBoxSeries series = new BlindBoxSeries();
        series.setName(request.getName());
        series.setDescription(request.getDescription());
        series.setCoverImage(request.getCoverImage());
        series.setPrice(request.getPrice());
        series.setStatus(1);
        seriesMapper.insert(series);
        return series.getId();
    }

    @Override
    public void updateSeries(Long id, SeriesUpdateRequestDto request) {
        getSeries(id);
        BlindBoxSeries series = new BlindBoxSeries();
        series.setId(id);
        series.setName(request.getName());
        series.setDescription(request.getDescription());
        series.setCoverImage(request.getCoverImage());
        series.setPrice(request.getPrice());
        seriesMapper.updateById(series);
    }

    @Override
    public Long createItem(Long seriesId, ItemCreateRequestDto request) {
        getSeries(seriesId);
        BlindBoxItem item = new BlindBoxItem();
        item.setSeriesId(seriesId);
        item.setSkuId(request.getSkuId());
        item.setStyleName(request.getStyleName());
        item.setImage(request.getImage());
        item.setProbability(request.getProbability());
        item.setIsHidden(request.getIsHidden());
        itemMapper.insert(item);
        checkProbabilitySum(seriesId);
        return item.getId();
    }

    @Override
    public void updateItem(Long itemId, ItemUpdateRequestDto request) {
        BlindBoxItem item = getItem(itemId);
        BlindBoxItem update = new BlindBoxItem();
        update.setId(itemId);
        update.setSkuId(request.getSkuId());
        update.setStyleName(request.getStyleName());
        update.setImage(request.getImage());
        update.setProbability(request.getProbability());
        update.setIsHidden(request.getIsHidden());
        itemMapper.updateById(update);
        checkProbabilitySum(item.getSeriesId());
    }

    @Override
    public void deleteItem(Long itemId) {
        getItem(itemId);
        itemMapper.deleteById(itemId);
    }

    private BlindBoxSeries getSeries(Long id) {
        BlindBoxSeries series = seriesMapper.selectById(id);
        if (series == null) {
            throw PromotionErrorCode.BLINDBOX_SERIES_NOT_FOUND.exception();
        }
        return series;
    }

    private BlindBoxItem getItem(Long id) {
        BlindBoxItem item = itemMapper.selectById(id);
        if (item == null) {
            throw PromotionErrorCode.BLINDBOX_ITEM_NOT_FOUND.exception();
        }
        return item;
    }

    private void checkProbabilitySum(Long seriesId) {
        List<BlindBoxItem> items = itemMapper.selectList(
                new LambdaQueryWrapper<BlindBoxItem>().eq(BlindBoxItem::getSeriesId, seriesId));
        int sum = items.stream().mapToInt(BlindBoxItem::getProbability).sum();
        if (sum > PROBABILITY_TOTAL) {
            throw PromotionErrorCode.PROBABILITY_SUM_ERROR.exception();
        }
    }
}
