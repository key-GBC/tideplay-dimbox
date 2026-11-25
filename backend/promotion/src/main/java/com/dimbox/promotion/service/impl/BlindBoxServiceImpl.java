package com.dimbox.promotion.service.impl;

import com.dimbox.common.result.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dimbox.common.exception.BizException;
import com.dimbox.promotion.client.ProductClient;
import com.dimbox.promotion.client.StockOperateRequest;
import com.dimbox.promotion.dto.blindbox.DrawRecordQueryDto;
import com.dimbox.promotion.dto.blindbox.DrawRecordResponseDto;
import com.dimbox.promotion.dto.blindbox.DrawResponseDto;
import com.dimbox.promotion.dto.blindbox.BlindBoxItemResponseDto;
import com.dimbox.promotion.dto.blindbox.SeriesDetailResponseDto;
import com.dimbox.promotion.entity.BlindBoxItem;
import com.dimbox.promotion.entity.BlindBoxOrder;
import com.dimbox.promotion.entity.BlindBoxSeries;
import com.dimbox.promotion.enums.PromotionErrorCode;
import com.dimbox.promotion.mapper.BlindBoxItemMapper;
import com.dimbox.promotion.mapper.BlindBoxOrderMapper;
import com.dimbox.promotion.mapper.BlindBoxSeriesMapper;
import com.dimbox.promotion.service.BlindBoxService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BlindBoxServiceImpl implements BlindBoxService {

    private static final int PROBABILITY_TOTAL = 10000;

    private final BlindBoxSeriesMapper seriesMapper;
    private final BlindBoxItemMapper itemMapper;
    private final BlindBoxOrderMapper orderMapper;
    private final ProductClient productClient;

    @Override
    public SeriesDetailResponseDto getSeriesDetail(Long id) {
        BlindBoxSeries series = getEnabledSeries(id);
        List<BlindBoxItem> items = itemMapper.selectList(
                new LambdaQueryWrapper<BlindBoxItem>()
                        .eq(BlindBoxItem::getSeriesId, id));

        SeriesDetailResponseDto dto = new SeriesDetailResponseDto();
        dto.setId(series.getId());
        dto.setName(series.getName());
        dto.setDescription(series.getDescription());
        dto.setCoverImage(series.getCoverImage());
        dto.setPrice(series.getPrice());
        dto.setItems(items.stream().map(this::toItemResponse).collect(Collectors.toList()));
        return dto;
    }

    @Override
    public DrawResponseDto drawBlindbox(Long userId, Long seriesId) {
        BlindBoxSeries series = getEnabledSeries(seriesId);
        List<BlindBoxItem> items = itemMapper.selectList(
                new LambdaQueryWrapper<BlindBoxItem>()
                        .eq(BlindBoxItem::getSeriesId, seriesId));
        if (items.isEmpty()) {
            throw PromotionErrorCode.BLINDBOX_ITEM_NOT_FOUND.exception();
        }

        BlindBoxItem selected = randomSelect(items);
        deductStock(selected.getSkuId());

        BlindBoxOrder order = new BlindBoxOrder();
        order.setOrderNo(String.valueOf(IdWorker.getId()));
        order.setUserId(userId);
        order.setSeriesId(seriesId);
        order.setItemId(selected.getId());
        order.setSkuId(selected.getSkuId());
        order.setPrice(series.getPrice());
        order.setIsHidden(selected.getIsHidden());
        try {
            orderMapper.insert(order);
        } catch (Exception e) {
            restoreStock(selected.getSkuId());
            throw e;
        }

        DrawResponseDto dto = new DrawResponseDto();
        dto.setOrderNo(order.getOrderNo());
        dto.setStyleName(selected.getStyleName());
        dto.setImage(selected.getImage());
        dto.setIsHidden(selected.getIsHidden());
        return dto;
    }

    @Override
    public PageResult<DrawRecordResponseDto> getDrawRecord(Long userId, DrawRecordQueryDto query) {
        Page<BlindBoxOrder> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<BlindBoxOrder> wrapper = new LambdaQueryWrapper<BlindBoxOrder>()
                .eq(BlindBoxOrder::getUserId, userId)
                .orderByDesc(BlindBoxOrder::getId);
        Page<BlindBoxOrder> result = orderMapper.selectPage(page, wrapper);
        List<DrawRecordResponseDto> records = result.getRecords().stream()
                .map(this::toRecordResponse)
                .collect(Collectors.toList());
        return PageResult.of(result.getTotal(), records, query.getPageNum(), query.getPageSize());
    }

    private BlindBoxSeries getEnabledSeries(Long id) {
        BlindBoxSeries series = seriesMapper.selectById(id);
        if (series == null || series.getStatus() == null || series.getStatus() != 1) {
            throw PromotionErrorCode.BLINDBOX_SERIES_NOT_FOUND.exception();
        }
        return series;
    }

    private BlindBoxItem randomSelect(List<BlindBoxItem> items) {
        int total = items.stream().mapToInt(BlindBoxItem::getProbability).sum();
        if (total != PROBABILITY_TOTAL) {
            throw PromotionErrorCode.PROBABILITY_SUM_ERROR.exception();
        }
        int r = ThreadLocalRandom.current().nextInt(PROBABILITY_TOTAL) + 1;
        int acc = 0;
        for (BlindBoxItem item : items) {
            acc += item.getProbability();
            if (r <= acc) {
                return item;
            }
        }
        return items.get(items.size() - 1);
    }

    private void deductStock(Long skuId) {
        try {
            productClient.deduct(new StockOperateRequest(skuId, 1));
        } catch (FeignException e) {
            if (e.status() == 409) {
                throw PromotionErrorCode.STOCK_NOT_ENOUGH.exception();
            }
            throw new BizException(500, "商品服务调用失败");
        }
    }

    private void restoreStock(Long skuId) {
        try {
            productClient.restore(new StockOperateRequest(skuId, 1));
        } catch (FeignException e) {
            // 回补失败记录日志，留待对账/人工兜底，不覆盖原始异常
            log.error("抽盒库存回补失败，skuId={}", skuId, e);
        }
    }

    private BlindBoxItemResponseDto toItemResponse(BlindBoxItem item) {
        BlindBoxItemResponseDto dto = new BlindBoxItemResponseDto();
        dto.setId(item.getId());
        dto.setStyleName(item.getStyleName());
        dto.setImage(item.getImage());
        return dto;
    }

    private DrawRecordResponseDto toRecordResponse(BlindBoxOrder order) {
        DrawRecordResponseDto dto = new DrawRecordResponseDto();
        dto.setOrderNo(order.getOrderNo());
        dto.setSeriesId(order.getSeriesId());
        dto.setItemId(order.getItemId());
        dto.setSkuId(order.getSkuId());
        dto.setIsHidden(order.getIsHidden());
        dto.setPrice(order.getPrice());
        dto.setCreatedAt(order.getCreatedAt());
        return dto;
    }
}
