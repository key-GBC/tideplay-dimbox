package com.dimbox.payment.client;


import com.dimbox.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 订单服务 Feign 客户端。
 * <p>{@code name} 对应 order 服务的应用名 dimbox-order，配合 Nacos 做服务发现与负载均衡。</p>
 */
@FeignClient(name = "dimbox-order")
public interface OrderClient {

    /** 按订单号查询订单信息，供创建支付单时校验归属、状态与金额 */
    @GetMapping("/order/internal/order-no/{orderNo}")
    Result<OrderInfoResponse> getOrderByNo(@PathVariable("orderNo") String orderNo);

    /** 支付成功后把订单推进为「已支付」 */
    @PostMapping("/order/internal/order-no/{orderNo}/mark-paid")
    Result<Void> markPaid(@PathVariable("orderNo") String orderNo);
}
