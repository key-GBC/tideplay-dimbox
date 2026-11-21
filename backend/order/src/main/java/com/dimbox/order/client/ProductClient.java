package com.dimbox.order.client;

import com.dimbox.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 商品服务 Feign 客户端。
 * <p>order 通过它调用 product 的 SKU 查询与库存扣减/回补接口，
 * name 必须是 product 注册到 Nacos 的服务名，Feign 会自动做负载均衡。</p>
 */
@FeignClient(name = "dimbox-product")
public interface ProductClient {

    /**
     * 查询 SKU 详情，用于下单时的快照与金额计算。
     *
     * @param skuId SKU id
     * @return SKU 详情
     */
    @GetMapping("/product/sku/{id}")
    Result<SkuDetailResponse> getSku(@PathVariable("id") Long skuId);

    /**
     * 原子扣减库存，库存不足时 product 返回 409。
     *
     * @param request 扣减请求（skuId + quantity）
     * @return 统一响应，无业务数据
     */
    @PostMapping("/product/stock/deduct")
    Result<Void> deduct(@RequestBody StockOperateRequest request);

    /**
     * 回补库存，用于取消订单或下单失败时的补偿。
     *
     * @param request 回补请求（skuId + quantity）
     * @return 统一响应，无业务数据
     */
    @PostMapping("/product/stock/restore")
    Result<Void> restore(@RequestBody StockOperateRequest request);
}
