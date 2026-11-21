package com.dimbox.promotion.service.impl;

import com.dimbox.common.exception.BizException;
import com.dimbox.promotion.entity.SeckillActivity;
import com.dimbox.promotion.enums.PromotionErrorCode;
import com.dimbox.promotion.mapper.SeckillActivityMapper;
import com.dimbox.promotion.mapper.SeckillOrderMapper;
import com.dimbox.promotion.mq.SeckillMessage;
import com.dimbox.promotion.mq.SeckillMessageProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeckillServiceImplTest {

    @Mock
    private SeckillActivityMapper seckillActivityMapper;
    @Mock
    private SeckillOrderMapper seckillOrderMapper;
    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private DefaultRedisScript<Long> seckillScript;
    @Mock
    private SeckillMessageProducer seckillMessageProducer;

    @InjectMocks
    private SeckillServiceImpl seckillService;

    private SeckillActivity activity;

    @BeforeEach
    void setUp() {
        activity = new SeckillActivity();
        activity.setId(1L);
        activity.setSkuId(100L);
        activity.setStock(10);
        activity.setStartTime(LocalDateTime.now().minusHours(1));
        activity.setEndTime(LocalDateTime.now().plusHours(1));
        // 让「查活动」始终返回这个时间范围内的活动
        when(seckillActivityMapper.selectById(1L)).thenReturn(activity);
    }

    @Test
    void buy_成功时返回订单号并发送消息() {
        when(stringRedisTemplate.execute(any(DefaultRedisScript.class), anyList(), any(Object[].class)))
                .thenReturn(1L);
        ValueOperations<String, String> valueOps = mock(ValueOperations.class);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOps);

        String orderNo = seckillService.buy(200L, 1L);

        assertThat(orderNo).isNotBlank();
        verify(seckillMessageProducer).send(any(SeckillMessage.class));
    }

    @Test
    void buy_库存不足时抛异常() {
        when(stringRedisTemplate.execute(any(DefaultRedisScript.class), anyList(), any(Object[].class)))
                .thenReturn(0L);

        assertThatThrownBy(() -> seckillService.buy(200L, 1L))
                .isInstanceOf(BizException.class)
                .hasMessage(PromotionErrorCode.STOCK_NOT_ENOUGH.getMessage());
    }

    @Test
    void buy_已抢购时返回已有订单号且不重复发消息() {
        when(stringRedisTemplate.execute(any(DefaultRedisScript.class), anyList(), any(Object[].class)))
                .thenReturn(-1L);

        ValueOperations<String, String> valueOps = mock(ValueOperations.class);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get(anyString())).thenReturn("order-123");

        String orderNo = seckillService.buy(200L, 1L);

        assertThat(orderNo).isEqualTo("order-123");
        verify(seckillMessageProducer, never()).send(any());
    }

    @Test
    void buy_活动不存在时抛异常() {
        // 覆盖 setUp 的 stub：让 selectById 返回 null，模拟活动不存在
        when(seckillActivityMapper.selectById(1L)).thenReturn(null);

        assertThatThrownBy(() -> seckillService.buy(200L, 1L))
                .isInstanceOf(BizException.class)
                .hasMessage(PromotionErrorCode.SECKILL_ACTIVITY_NOT_FOUND.getMessage());
    }
}
