package com.dimbox.order.controller;

import com.dimbox.common.exception.BizException;
import com.dimbox.common.result.ErrorCode;
import com.dimbox.common.result.PageResult;
import com.dimbox.common.result.Result;
import com.dimbox.order.dto.CreateOrderRequestDto;
import com.dimbox.order.dto.OrderDetailResponseDto;
import com.dimbox.order.dto.OrderListItemDto;
import com.dimbox.order.dto.OrderListQueryDto;
import com.dimbox.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单接口。
 * <p>全部接口需登录，身份经网关透传的 X-User-Id 获取。</p>
 */
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 提交订单，返回订单号。
     */
    @PostMapping
    public Result<String> createOrder(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                      @Valid @RequestBody CreateOrderRequestDto request) {
        checkUser(userId);
        return Result.success(orderService.createOrder(userId, request));
    }

    /**
     * 分页查询当前用户订单。
     */
    @GetMapping
    public Result<PageResult<OrderListItemDto>> page(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                                     OrderListQueryDto query) {
        checkUser(userId);
        return Result.success(orderService.page(userId, query));
    }

    /**
     * 查询订单详情。
     */
    @GetMapping("/{id}")
    public Result<OrderDetailResponseDto> detail(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                                 @PathVariable("id") Long orderId) {
        checkUser(userId);
        return Result.success(orderService.detail(userId, orderId));
    }

    /**
     * 取消订单。
     */
    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                               @PathVariable("id") Long orderId){
        checkUser(userId);
        orderService.cancel(userId, orderId);
        return Result.success();
    }

    private void checkUser(Long userId) {
        if (userId == null){
            throw new BizException(ErrorCode.UNAUTHORIZED);
        }
    }
}
