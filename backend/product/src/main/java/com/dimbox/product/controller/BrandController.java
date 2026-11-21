package com.dimbox.product.controller;

import com.dimbox.common.result.Result;
import com.dimbox.product.dto.BrandRequestDto;
import com.dimbox.product.dto.BrandResponseDto;
import com.dimbox.product.service.BrandService;
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

import java.util.List;

@RestController
@RequestMapping("/product/brand")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @GetMapping("/list")
    public Result<List<BrandResponseDto>> list(){
        return Result.success(brandService.list());
    }

    @PostMapping
    public Result<Long> add(@Valid @RequestBody BrandRequestDto request){
        return Result.success(brandService.add(request));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable("id") Long id,
                               @Valid @RequestBody BrandRequestDto request) {
        brandService.update(id, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id){
        brandService.delete(id);
        return Result.success();
    }

}
