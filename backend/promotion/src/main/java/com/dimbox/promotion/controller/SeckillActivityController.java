package com.dimbox.promotion.controller;

import com.dimbox.common.result.PageResult;
import com.dimbox.common.result.Result;
import com.dimbox.promotion.dto.CreateSeckillActivityRequestDto;
import com.dimbox.promotion.dto.SeckillActivityResponseDto;
import com.dimbox.promotion.dto.SeckillActivityQueryRequestDto;
import com.dimbox.promotion.service.SeckillActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/promotion/seckill")
@RequiredArgsConstructor
public class SeckillActivityController {

    private final SeckillActivityService seckillActivityService;

    @PostMapping("/activity")
    public Result<Long> createSeckillActivity(@Valid @RequestBody CreateSeckillActivityRequestDto request) {
        return Result.success(seckillActivityService.create(request));
    }

    @GetMapping("/activity")
    public Result<PageResult<SeckillActivityResponseDto>> getSeckillActivityList(SeckillActivityQueryRequestDto query) {
        return Result.success(seckillActivityService.page(query));
    }

    @PostMapping("/activity/{id}/warmup")
    public Result<Void> warmup(@PathVariable("id") Long activityId) {
        seckillActivityService.warmup(activityId);
        return Result.success();
    }

}
