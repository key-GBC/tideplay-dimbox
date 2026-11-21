package com.dimbox.promotion.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dimbox.common.result.PageResult;
import com.dimbox.promotion.dto.SeckillActivityResponseDto;
import com.dimbox.promotion.dto.SeckillOrderResponseDto;
import com.dimbox.promotion.dto.SeckillRecordQueryDto;
import com.dimbox.promotion.entity.SeckillActivity;
import com.dimbox.promotion.entity.SeckillOrder;
import com.dimbox.promotion.enums.PromotionErrorCode;
import com.dimbox.promotion.mapper.SeckillActivityMapper;
import com.dimbox.promotion.mapper.SeckillOrderMapper;
import com.dimbox.promotion.mq.SeckillMessage;
import com.dimbox.promotion.mq.SeckillMessageProducer;
import com.dimbox.promotion.service.SeckillService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeckillServiceImpl implements SeckillService {

    private final SeckillActivityMapper seckillActivityMapper;
    private final SeckillOrderMapper seckillOrderMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final DefaultRedisScript<Long> seckillScript;
    private final SeckillMessageProducer seckillMessageProducer;

    @Override
    public String buy(Long userId, Long activityId) {
        SeckillActivity activity = seckillActivityMapper.selectById(activityId);
        if (activity == null) {
            throw PromotionErrorCode.SECKILL_ACTIVITY_NOT_FOUND.exception();
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(activity.getStartTime())) {
            throw PromotionErrorCode.SECKILL_NOT_STARTED.exception();
        }
        if (now.isAfter(activity.getEndTime())) {
            throw PromotionErrorCode.SECKILL_ENDED.exception();
        }

        Long result = stringRedisTemplate.execute(
                seckillScript,
                List.of("seckill:stock:" + activityId, "seckill:users:" + activityId),
                String.valueOf(userId), "1");

        if (result == null || result == 0) {
            throw PromotionErrorCode.STOCK_NOT_ENOUGH.exception();
        }
        if (result == -1) {
            String existOrderNo = stringRedisTemplate.opsForValue()
                    .get("seckill:order:" + activityId + ":" + userId);
            if (existOrderNo != null) {
                return existOrderNo;
            }
            // Redis 里订单号丢失时回退查 DB（异步落库后这里能查到）
            SeckillOrder dbOrder = seckillOrderMapper.selectOne(
                    new LambdaQueryWrapper<SeckillOrder>()
                            .eq(SeckillOrder::getActivityId, activityId)
                            .eq(SeckillOrder::getUserId, userId));
            return dbOrder != null ? dbOrder.getOrderNo() : null;
        }

        String orderNo = String.valueOf(IdWorker.getId());
        stringRedisTemplate.opsForValue()
                .set("seckill:order:" + activityId + ":" + userId, orderNo);
        SeckillMessage message = new SeckillMessage(activityId, userId, activity.getSkuId(), orderNo);
        seckillMessageProducer.send(message);
        return orderNo;
    }

    @Override
    public PageResult<SeckillOrderResponseDto> pageRecord(Long userId, SeckillRecordQueryDto query) {
        Page<SeckillOrder> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<SeckillOrder> wrapper = new LambdaQueryWrapper<SeckillOrder>()
                .eq(SeckillOrder::getUserId , userId)
                .orderByDesc(SeckillOrder::getId);
        Page<SeckillOrder> result = seckillOrderMapper.selectPage(page, wrapper);
        List<SeckillOrderResponseDto> items = result.getRecords().stream()
                .map(this::toRecord)
                .collect(Collectors.toList());
        return PageResult.of(result.getTotal(), items, query.getPageNum(), query.getPageSize());
    }

    private SeckillOrderResponseDto toRecord(SeckillOrder order) {
        SeckillOrderResponseDto dto = new SeckillOrderResponseDto();
        dto.setId(order.getId());
        dto.setActivityId(order.getActivityId());
        dto.setSkuId(order.getSkuId());
        dto.setOrderNo(order.getOrderNo());
        dto.setStatus(order.getStatus());
        dto.setCreateAt(order.getCreatedAt());
        return dto;
    }

}
