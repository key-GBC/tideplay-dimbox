package com.dimbox.product.controller;

import com.dimbox.common.result.Result;
import com.dimbox.common.result.PageResult;
import com.dimbox.product.dto.ProductDetailResponseDto;
import com.dimbox.product.dto.ProductListItemDto;
import com.dimbox.product.dto.ProductListQueryDto;
import com.dimbox.product.dto.ProductSaveRequestDto;
import com.dimbox.product.dto.SkuRequestDto;
import com.dimbox.product.dto.StatusRequestDto;
import com.dimbox.product.service.ProductService;
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
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductAdminController {


    private final ProductService productService;

    @GetMapping("/admin/list")
    public Result<PageResult<ProductListItemDto>> adminPage(ProductListQueryDto query) {
        return Result.success(productService.adminPage(query));
    }

    @GetMapping("/admin/{id}")
    public Result<ProductDetailResponseDto> adminDetail(@PathVariable("id") Long id) {
        return Result.success(productService.adminDetail(id));
    }

    @PostMapping("/{id}/sku")
    public Result<Long> addSku(@PathVariable("id") Long productId,
                               @Valid @RequestBody SkuRequestDto request) {
        return Result.success(productService.addSku(productId, request));
    }

    @PutMapping("/sku/{skuId}")
    public Result<Void> updateSku(@PathVariable("skuId") Long skuId,
                                  @Valid @RequestBody SkuRequestDto request) {
        productService.updateSku(skuId, request);
        return Result.success();
    }

    @PutMapping("/sku/{skuId}/status")
    public Result<Void> updateSkuStatus(@PathVariable("skuId") Long skuId,
                                        @Valid @RequestBody StatusRequestDto request) {
        productService.updateSkuStatus(skuId, request.getStatus());
        return Result.success();
    }

    @DeleteMapping("/sku/{skuId}")
    public Result<Void> deleteSku(@PathVariable("skuId") Long skuId) {
        productService.deleteSku(skuId);
        return Result.success();
    }

    @PostMapping
    public Result<Long> create(@Valid @RequestBody ProductSaveRequestDto request){
        return Result.success(productService.create(request));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable("id") Long id,
                               @Valid @RequestBody ProductSaveRequestDto request){
        productService.update(id, request);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable("id") Long id,
                                     @Valid @RequestBody StatusRequestDto request) {
        productService.updateStatus(id, request.getStatus());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id){
        productService.delete(id);
        return Result.success();
    }
}
