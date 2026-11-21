package com.dimbox.promotion.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dimbox.common.exception.BizException;
import com.dimbox.common.result.PageResult;
import com.dimbox.promotion.client.ProductClient;
import com.dimbox.promotion.client.SkuInfoResponse;
import com.dimbox.promotion.dto.CreateSeckillActivityRequestDto;
import com.dimbox.promotion.dto.SeckillActivityQueryRequestDto;
import com.dimbox.promotion.dto.SeckillActivityResponseDto;
import com.dimbox.promotion.entity.SeckillActivity;
import com.dimbox.promotion.enums.PromotionErrorCode;
import com.dimbox.promotion.mapper.SeckillActivityMapper;
import com.dimbox.promotion.service.SeckillActivityService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeckillActivityServiceImpl implements SeckillActivityService {

    private final SeckillActivityMapper seckillActivityMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ProductClient productClient;

    @Override
    public Long create(CreateSeckillActivityRequestDto dto){
        if(dto.getStartTime().isAfter(dto.getEndTime())){
            throw new BizException(400, "开始时间不能晚于结束时间");
        }
        SkuInfoResponse sku = getSku(dto.getSkuId());
        if (sku == null) {
            throw new BizException(404, "商品款式不存在");
        }
        if (dto.getStock() > sku.getStock()) {
            throw new BizException(409, "秒杀库存不能超过商品库存");
        }
        SeckillActivity seckillActivity = new SeckillActivity();
        seckillActivity.setSkuId(dto.getSkuId());
        seckillActivity.setSeckillPrice(dto.getSeckillPrice());
        seckillActivity.setStock(dto.getStock());
        seckillActivity.setStartTime(dto.getStartTime());
        seckillActivity.setEndTime(dto.getEndTime());
        seckillActivityMapper.insert(seckillActivity);
        return seckillActivity.getId();
    }

    @Override
    public PageResult<SeckillActivityResponseDto> page(SeckillActivityQueryRequestDto query) {
        Page<SeckillActivity> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<SeckillActivity> wrapper = new LambdaQueryWrapper<SeckillActivity>()
                .eq(query.getStatus() != null, SeckillActivity::getStatus, query.getStatus())
                .orderByDesc(SeckillActivity::getId);
        Page<SeckillActivity> result = seckillActivityMapper.selectPage(page, wrapper);
        List<SeckillActivityResponseDto> items = result.getRecords().stream()
                .map(this::toListItem)
                .collect(Collectors.toList());
        return PageResult.of(result.getTotal(), items, query.getPageNum(), query.getPageSize());
    }

    @Override
    public void warmup(Long activityId){
        SeckillActivity activity = seckillActivityMapper.selectById(activityId);
        if (activity == null) {
            throw PromotionErrorCode.SECKILL_ACTIVITY_NOT_FOUND.exception();
        }

        String key = "seckill:stock:" + activityId;
        stringRedisTemplate.opsForValue().set(key, String.valueOf(activity.getStock()));
    }

    private SeckillActivityResponseDto toListItem(SeckillActivity seckillActivity) {
        SeckillActivityResponseDto dto = new SeckillActivityResponseDto();
        dto.setId(seckillActivity.getId());
        dto.setSkuId(seckillActivity.getSkuId());
        dto.setSeckillPrice(seckillActivity.getSeckillPrice());
        dto.setStock(seckillActivity.getStock());
        dto.setStartTime(seckillActivity.getStartTime());
        dto.setEndTime(seckillActivity.getEndTime());
        dto.setStatus(seckillActivity.getStatus());
        return dto;
    }

    private SkuInfoResponse getSku(Long skuId) {
        try {
            SkuInfoResponse sku = productClient.getSku(skuId).getData();
            if (sku == null) {
                throw new BizException(404, "商品款式不存在");
            }
            return sku;
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new BizException(404, "商品款式不存在");
            }
            throw new BizException(500, "商品服务调用失败");
        }
    }

}
