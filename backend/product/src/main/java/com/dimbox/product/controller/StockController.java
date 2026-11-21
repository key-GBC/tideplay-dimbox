package com.dimbox.product.controller;

import com.dimbox.common.result.Result;
import com.dimbox.product.dto.StockDeductRequestDto;
import com.dimbox.product.dto.StockIncreaseRequestDto;
import com.dimbox.product.dto.StockRestoreRequestDto;
import com.dimbox.product.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    /**
     * 扣减库存的接口方法
     *
     * @param request 包含库存扣减信息的请求体，使用@Valid进行校验
     * @return 返回操作结果，成功时返回成功状态
     * @PostMapping("/deduct") 表示这是一个HTTP POST请求，映射到"/deduct"路径
     */
    @PostMapping("/deduct")
    public Result<Void> deduct(@Valid @RequestBody StockDeductRequestDto request) {
    // 调用库存服务执行库存扣减操作
    // 参数：商品SKU ID和需要扣减的数量
        stockService.deduct(request.getSkuId(), request.getQuantity());
    // 返回成功操作结果
        return Result.success();
    }

    /**
     * 回补库存（内部调用）。
     */
    @PostMapping("/restore")
    public Result<Void> restore(@Valid @RequestBody StockRestoreRequestDto request) {
        stockService.restore(request.getSkuId(), request.getQuantity());
        return Result.success();
    }

    /**
     * 增加库存（内部调用）。
     */
    @PostMapping("/increase")
    public Result<Void> increase(@Valid @RequestBody StockIncreaseRequestDto request) {
        stockService.increase(request.getSkuId(), request.getQuantity());
        return Result.success();
    }
}
