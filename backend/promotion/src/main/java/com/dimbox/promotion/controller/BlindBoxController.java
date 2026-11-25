package com.dimbox.promotion.controller;

import com.dimbox.common.result.PageResult;
import com.dimbox.common.result.Result;
import com.dimbox.promotion.dto.blindbox.DrawRecordQueryDto;
import com.dimbox.promotion.dto.blindbox.DrawRecordResponseDto;
import com.dimbox.promotion.dto.blindbox.DrawResponseDto;
import com.dimbox.promotion.dto.blindbox.SeriesDetailResponseDto;
import com.dimbox.promotion.service.BlindBoxService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/promotion/blindbox")
@RequiredArgsConstructor
public class BlindBoxController {

    private final BlindBoxService blindBoxService;

    @GetMapping("/{id}")
    public Result<SeriesDetailResponseDto> getSeriesDetail(@PathVariable("id") Long id) {
        return Result.success(blindBoxService.getSeriesDetail(id));
    }

    @PostMapping("/{seriesId}/draw")
    public Result<DrawResponseDto> drawBlindbox(@RequestHeader("X-User-Id") Long userId,
                                                @PathVariable("seriesId") Long seriesId) {
        return Result.success(blindBoxService.drawBlindbox(userId, seriesId));
    }

    @GetMapping("/record")
    public Result<PageResult<DrawRecordResponseDto>> getDrawRecord(@RequestHeader("X-User-Id") Long userId,
                                                                   DrawRecordQueryDto query) {
        return Result.success(blindBoxService.getDrawRecord(userId, query));
    }
}
