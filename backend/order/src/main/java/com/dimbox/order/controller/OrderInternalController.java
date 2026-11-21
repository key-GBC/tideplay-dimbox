package com.dimbox.order.controller;

import com.dimbox.common.result.Result;
import com.dimbox.order.dto.OrderInfoResponse;
import com.dimbox.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单内部接口。
 * <p>仅服务间 Feign 直连使用，不加入网关白名单（走 lb://dimbox-order）。</p>
 */
@RestController
@RequestMapping("/order/internal")
@RequiredArgsConstructor
public class OrderInternalController {

    private final OrderService orderService;

    @GetMapping("/order-no/{orderNo}")
    public Result<OrderInfoResponse> getByOrderNo(@PathVariable("orderNo") String orderNo) {
        return Result.success(orderService.getOrderByOrderNo(orderNo));
    }

    @PostMapping("/order-no/{orderNo}/mark-paid")
    public Result<Void> markPaid(@PathVariable("orderNo") String orderNo) {
        orderService.markPaid(orderNo);
        return Result.success();
    }
}
