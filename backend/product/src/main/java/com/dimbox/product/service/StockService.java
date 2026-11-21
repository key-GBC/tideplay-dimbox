package com.dimbox.product.service;

/**
 * 库存服务接口。
 * <p>提供库存的原子扣减能力，供后续订单模块通过内部调用使用。</p>
 */
public interface StockService {

    /**
     * 原子扣减库存。
     * <p>通过单条 UPDATE 保证并发安全，扣减后库存不会为负；库存不足时抛出业务异常。</p>
     *
     * @param skuId    SKU id
     * @param quantity 扣减数量
     */
    void deduct(Long skuId, Integer quantity);

    /**
     * 回补库存。
     * <p>订单取消、下单失败等场景回补已扣减的库存。</p>
     *
     * @param skuId    SKU id
     * @param quantity 回补数量
     */
    void restore(Long skuId, Integer quantity);

    /**
     * 增加库存（补货/入库）。
     * <p>供运营后台在商品补货时调用，与「回补库存」区分，便于权限与审计管理。</p>
     *
     * @param skuId    SKU id
     * @param quantity 增加数量
     */
    void increase(Long skuId, Integer quantity);
}
