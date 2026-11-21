package com.dimbox.product.service.impl;

import com.dimbox.product.enums.ProductErrorCode;
import com.dimbox.product.mapper.ProductSkuMapper;
import com.dimbox.product.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 库存服务实现。
 * <p>扣减逻辑依赖 ProductSkuMapper 的原子 SQL，保证并发下不超卖。</p>
 */
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final ProductSkuMapper productSkuMapper;

    @Override
    public void deduct(Long skuId, Integer quantity) {
        // deductStock 返回受影响行数：0 表示库存不足或 SKU 不存在
        int affected = productSkuMapper.deductStock(skuId, quantity);
        if (affected == 0) {
            throw ProductErrorCode.STOCK_NOT_ENOUGH.exception();
        }
    }

    /**
     * 回补库存。
     */
    @Override
    public void restore(Long skuId, Integer quantity) {
        productSkuMapper.restoreStock(skuId, quantity);
    }

    /**
     * 增加库存。
     */
    @Override
    public void increase(Long skuId, Integer quantity) {
        productSkuMapper.increaseStock(skuId, quantity);
    }
}
