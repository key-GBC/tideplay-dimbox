package com.dimbox.order.service;

import com.dimbox.common.result.PageResult;
import com.dimbox.order.dto.*;

/**
 * 订单服务接口。
 * <p>提供下单、列表、详情与取消能力，下单涉及跨服务扣减库存。</p>
 */
public interface OrderService {

    /**
     * 提交订单：扣减库存并生成订单与明细。
     *
     * @param userId  下单用户 id
     * @param request 下单请求
     * @return 订单号
     */
    String createOrder(Long userId, CreateOrderRequestDto request);

    /**
     * 分页查询当前用户订单。
     *
     * @param userId 用户 id
     * @param query  分页与状态过滤条件
     * @return 订单分页结果
     */
    PageResult<OrderListItemDto> page(Long userId, OrderListQueryDto query);

    /**
     * 查询订单详情。
     *
     * @param userId  用户 id
     * @param orderId 订单 id
     * @return 订单详情
     */
    OrderDetailResponseDto detail(Long userId, Long orderId);

    /**
     * 取消待支付订单并回补库存。
     *
     * @param userId  用户 id
     * @param orderId 订单 id
     */
    void cancel(Long userId, Long orderId);

    /**
     * 按订单号查询订单信息（内部调用，供支付服务校验）。
     *
     * @param orderNo 订单号
     * @return 订单信息
     */
    OrderInfoResponse getOrderByOrderNo(String orderNo);

    /**
     * 把订单标记为已支付（内部调用，供支付服务回调）。
     * <p>幂等：已是「已支付」直接返回；非「待支付」状态报错。</p>
     *
     * @param orderNo 订单号
     */
    void markPaid(String orderNo);

    PageResult<OrderAdminListDto> adminPage(OrderListQueryDto query);
}
