package com.dimbox.promotion.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.dimbox.common.result.PageResult;
import com.dimbox.common.result.Result;
import com.dimbox.promotion.dto.SeckillBuyResponseDto;
import com.dimbox.promotion.dto.SeckillOrderResponseDto;
import com.dimbox.promotion.dto.SeckillRecordQueryDto;
import com.dimbox.promotion.service.SeckillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/promotion/seckill")
@RequiredArgsConstructor
public class SeckillController {

    private final SeckillService seckillService;

    @SentinelResource(value = "seckill", blockHandler = "seckillBlock")
    @PostMapping("/{activityId}/buy")
    public Result<SeckillBuyResponseDto> buy(@RequestHeader("X-User-Id") Long userId,
                                             @PathVariable("activityId") Long activityId) {
        SeckillBuyResponseDto responseDto = new SeckillBuyResponseDto();
        responseDto.setOrderNo(seckillService.buy(userId, activityId));
        return Result.success(responseDto);
    }

    @GetMapping("/record")
    public Result<PageResult<SeckillOrderResponseDto>> myRecords(@RequestHeader("X-User-Id") Long userId,
                                                                 SeckillRecordQueryDto query) {
        return Result.success(seckillService.pageRecord(userId, query));
    }

    public Result<SeckillBuyResponseDto> seckillBlock(Long userId, Long activityId, BlockException ex) {
        return Result.fail(429, "请求过于频繁");
    }
}
