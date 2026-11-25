package com.dimbox.promotion.client;

import com.dimbox.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "dimbox-product")
public interface ProductClient {

    @PostMapping("/product/stock/deduct")
    Result<Void> deduct(@RequestBody StockOperateRequest request);

    @PostMapping("/product/stock/restore")
    Result<Void> restore(@RequestBody StockOperateRequest request);

    @GetMapping("/product/sku/{id}")
    Result<SkuInfoResponse> getSku(@PathVariable("id") Long skuId);
}
