package com.dimbox.order.controller;

import com.dimbox.common.result.PageResult;
import com.dimbox.common.result.Result;
import com.dimbox.order.dto.OrderAdminListDto;
import com.dimbox.order.dto.OrderListQueryDto;
import com.dimbox.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order/admin")
@RequiredArgsConstructor
public class OrderAdminController {

    private final OrderService orderService;

    @GetMapping("/list")
    public Result<PageResult<OrderAdminListDto>> adminPage(OrderListQueryDto query) {
        return Result.success(orderService.adminPage(query));
    }
}
