package com.dimbox.promotion.controller;

import com.dimbox.common.result.Result;
import com.dimbox.common.result.PageResult;
import com.dimbox.promotion.dto.blindbox.ItemCreateRequestDto;
import com.dimbox.promotion.dto.blindbox.ItemUpdateRequestDto;
import com.dimbox.promotion.dto.blindbox.SeriesCreateRequestDto;
import com.dimbox.promotion.dto.blindbox.SeriesAdminDetailResponseDto;
import com.dimbox.promotion.dto.blindbox.SeriesListQueryDto;
import com.dimbox.promotion.dto.blindbox.SeriesListResponseDto;
import com.dimbox.promotion.dto.blindbox.SeriesUpdateRequestDto;
import com.dimbox.promotion.service.BlindBoxAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/promotion/blindbox")
@RequiredArgsConstructor
public class BlindBoxAdminController {

    private final BlindBoxAdminService blindBoxAdminService;

    @GetMapping("/admin/series/list")
    public Result<PageResult<SeriesListResponseDto>> listSeries(SeriesListQueryDto query) {
        return Result.success(blindBoxAdminService.adminPageSeries(query));
    }

    @GetMapping("/admin/series/{id}")
    public Result<SeriesAdminDetailResponseDto> getSeriesDetail(@PathVariable("id") Long id) {
        return Result.success(blindBoxAdminService.adminGetSeriesDetail(id));
    }

    @PostMapping("/series")
    public Result<Long> seriesCreate(@Valid @RequestBody SeriesCreateRequestDto request) {
        return Result.success(blindBoxAdminService.createSeries(request));
    }

    @PostMapping("/series/{id}/item")
    public Result<Long> itemCreate(@PathVariable("id") Long seriesId,
                                   @Valid @RequestBody ItemCreateRequestDto request) {
        return Result.success(blindBoxAdminService.createItem(seriesId, request));
    }

    @PutMapping("/series/{id}")
    public Result<Void> seriesUpdate(@PathVariable("id") Long id,
                                     @Valid @RequestBody SeriesUpdateRequestDto request) {
        blindBoxAdminService.updateSeries(id, request);
        return Result.success();
    }

    @PutMapping("/item/{itemId}")
    public Result<Void> itemUpdate(@PathVariable("itemId") Long itemId,
                                   @Valid @RequestBody ItemUpdateRequestDto request) {
        blindBoxAdminService.updateItem(itemId, request);
        return Result.success();
    }

    @DeleteMapping("/item/{itemId}")
    public Result<Void> itemDelete(@PathVariable("itemId") Long itemId) {
        blindBoxAdminService.deleteItem(itemId);
        return Result.success();
    }
}
