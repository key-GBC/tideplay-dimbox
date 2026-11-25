package com.dimbox.promotion.service.impl;

import com.dimbox.common.exception.BizException;
import com.dimbox.common.result.Result;
import com.dimbox.promotion.client.ProductClient;
import com.dimbox.promotion.client.StockOperateRequest;
import com.dimbox.promotion.dto.blindbox.DrawResponseDto;
import com.dimbox.promotion.entity.BlindBoxItem;
import com.dimbox.promotion.entity.BlindBoxOrder;
import com.dimbox.promotion.entity.BlindBoxSeries;
import com.dimbox.promotion.enums.PromotionErrorCode;
import com.dimbox.promotion.mapper.BlindBoxItemMapper;
import com.dimbox.promotion.mapper.BlindBoxOrderMapper;
import com.dimbox.promotion.mapper.BlindBoxSeriesMapper;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlindBoxServiceImplTest {

    @Mock
    private BlindBoxSeriesMapper seriesMapper;
    @Mock
    private BlindBoxItemMapper itemMapper;
    @Mock
    private BlindBoxOrderMapper orderMapper;
    @Mock
    private ProductClient productClient;

    @InjectMocks
    private BlindBoxServiceImpl blindBoxService;

    private BlindBoxSeries series;

    @BeforeEach
    void setUp() {
        series = new BlindBoxSeries();
        series.setId(1L);
        series.setName("测试系列");
        series.setPrice(5900);
        series.setStatus(1);
        when(seriesMapper.selectById(1L)).thenReturn(series);
    }

    @Test
    void drawBlindbox_成功时扣库存并落库() {
        when(itemMapper.selectList(any()))
                .thenReturn(List.of(item(1L, 100L, 5000), item(2L, 101L, 5000)));
        when(productClient.deduct(any(StockOperateRequest.class))).thenReturn(Result.success());

        DrawResponseDto result = blindBoxService.drawBlindbox(200L, 1L);

        assertThat(result.getOrderNo()).isNotBlank();
        assertThat(result.getStyleName()).isNotBlank();
        verify(productClient).deduct(any(StockOperateRequest.class));
        verify(orderMapper).insert(any(BlindBoxOrder.class));
    }

    @Test
    void drawBlindbox_系列不存在时抛异常() {
        when(seriesMapper.selectById(1L)).thenReturn(null);

        assertThatThrownBy(() -> blindBoxService.drawBlindbox(200L, 1L))
                .isInstanceOf(BizException.class)
                .hasMessage(PromotionErrorCode.BLINDBOX_SERIES_NOT_FOUND.getMessage());
        verify(productClient, never()).deduct(any());
    }

    @Test
    void drawBlindbox_系列无款式时抛异常() {
        when(itemMapper.selectList(any())).thenReturn(List.of());

        assertThatThrownBy(() -> blindBoxService.drawBlindbox(200L, 1L))
                .isInstanceOf(BizException.class)
                .hasMessage(PromotionErrorCode.BLINDBOX_ITEM_NOT_FOUND.getMessage());
    }

    @Test
    void drawBlindbox_概率和不为10000时抛异常() {
        when(itemMapper.selectList(any()))
                .thenReturn(List.of(item(1L, 100L, 5000), item(2L, 101L, 4000)));

        assertThatThrownBy(() -> blindBoxService.drawBlindbox(200L, 1L))
                .isInstanceOf(BizException.class)
                .hasMessage(PromotionErrorCode.PROBABILITY_SUM_ERROR.getMessage());
    }

    @Test
    void drawBlindbox_库存不足时抛异常() {
        FeignException feignException = mock(FeignException.class);
        when(feignException.status()).thenReturn(409);
        when(itemMapper.selectList(any())).thenReturn(List.of(item(1L, 100L, 10000)));
        when(productClient.deduct(any(StockOperateRequest.class))).thenThrow(feignException);

        assertThatThrownBy(() -> blindBoxService.drawBlindbox(200L, 1L))
                .isInstanceOf(BizException.class)
                .hasMessage(PromotionErrorCode.STOCK_NOT_ENOUGH.getMessage());
    }

    @Test
    void drawBlindbox_落库失败时回补库存() {
        when(itemMapper.selectList(any())).thenReturn(List.of(item(1L, 100L, 10000)));
        when(productClient.deduct(any(StockOperateRequest.class))).thenReturn(Result.success());
        when(orderMapper.insert(any(BlindBoxOrder.class))).thenThrow(new RuntimeException("db error"));

        assertThatThrownBy(() -> blindBoxService.drawBlindbox(200L, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("db error");
        verify(productClient).restore(any(StockOperateRequest.class));
    }

    private BlindBoxItem item(Long id, Long skuId, int probability) {
        BlindBoxItem item = new BlindBoxItem();
        item.setId(id);
        item.setSeriesId(1L);
        item.setSkuId(skuId);
        item.setStyleName("款式" + id);
        item.setProbability(probability);
        item.setIsHidden(0);
        return item;
    }
}
