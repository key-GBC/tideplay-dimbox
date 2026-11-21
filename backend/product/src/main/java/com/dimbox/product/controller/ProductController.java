package com.dimbox.product.controller;

import com.dimbox.common.result.PageResult;
import com.dimbox.common.result.Result;
import com.dimbox.product.dto.ProductDetailResponseDto;
import com.dimbox.product.dto.ProductListItemDto;
import com.dimbox.product.dto.ProductListQueryDto;
import com.dimbox.product.dto.SkuDetailResponseDto;
import com.dimbox.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/list")
    public Result<PageResult<ProductListItemDto>> page(ProductListQueryDto query) {
        return Result.success(productService.page(query));
    }

    @GetMapping("/{id}")
    public Result<ProductDetailResponseDto> detail(@PathVariable("id") Long id) {
        return Result.success(productService.detail(id));
    }

    /**
     * 查询单个 SKU 详情（内部调用）。
     */
    @GetMapping("/sku/{id}")
    public Result<SkuDetailResponseDto> skuDetail(@PathVariable("id") Long skuId) {
        return Result.success(productService.getSkuDetail(skuId));
    }
}
