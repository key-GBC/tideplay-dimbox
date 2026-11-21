package com.dimbox.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.dimbox.common.result.PageResult;
import com.dimbox.common.exception.BizException;
import com.dimbox.order.client.ProductClient;
import com.dimbox.order.client.SkuDetailResponse;
import com.dimbox.order.client.StockOperateRequest;
import com.dimbox.order.dto.*;
import com.dimbox.order.entity.Order;
import com.dimbox.order.entity.OrderItem;
import com.dimbox.order.enums.OrderStatus;
import com.dimbox.order.enums.OrderErrorCode;
import com.dimbox.order.mapper.OrderItemMapper;
import com.dimbox.order.mapper.OrderMapper;
import com.dimbox.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DuplicateKeyException;
import feign.FeignException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单服务实现。
 * <p>下单按「幂等检查 → 查 SKU → 扣库存 → 建单」顺序执行；库存扣减跨服务，
 * 因此建单失败或重复提交时需要回补库存以保证最终一致。</p>
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    /** 订单明细表 Mapper */
    private final OrderItemMapper orderItemMapper;
    /** 订单表 Mapper */
    private final OrderMapper orderMapper;
    /** 商品服务 Feign 客户端 */
    private final ProductClient productClient;

    @Override
    @Transactional
    public String createOrder(Long userId, CreateOrderRequestDto request) {
        // 1. 幂等预检查：命中直接返回原订单号，不重复扣库存
        Order existing = orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getRequestId, request.getRequestId()));
        if (existing != null) {
            return existing.getOrderNo();
        }

        // 2. 查询 SKU，金额与快照以服务端数据为准
        SkuDetailResponse sku = getSku(request.getSkuId());
        if (sku.getStatus() == null || sku.getStatus() != 1) {
            throw OrderErrorCode.SKU_NOT_FOUND.exception();
        }

        // 3. 原子扣减库存
        deductStock(request.getSkuId(), request.getQuantity());

        // 4. 创建订单与明细；重复提交或失败时回补库存
        Order order = buildOrder(userId, request, sku);
        try {
            orderMapper.insert(order);
            orderItemMapper.insert(buildItem(order.getId(), request, sku));
        } catch (DuplicateKeyException e) {
            // 并发重复提交：撞了 request_id 唯一索引，回补刚扣的库存
            restoreStock(request.getSkuId(), request.getQuantity());
            Order duplicate = orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                    .eq(Order::getRequestId, request.getRequestId()));
            if (duplicate != null) {
                return duplicate.getOrderNo();
            }
            throw OrderErrorCode.ORDER_DUPLICATE.exception();
        } catch (Exception e) {
            // 建单失败：回补库存后继续抛
            restoreStock(request.getSkuId(), request.getQuantity());
            throw e;
        }
        return order.getOrderNo();
    }

    @Override
    public PageResult<OrderListItemDto> page(Long userId, OrderListQueryDto query) {
        // 1. 构造分页参数：第几页、每页多少条
        Page<Order> page = new Page<>(query.getPageNum(), query.getPageSize());
        // 2. 构造查询条件：只查自己的订单；status 不为空才加状态过滤；按 id 倒序
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .eq(query.getStatus() != null, Order::getStatus, query.getStatus())
                .orderByDesc(Order::getId);
        // 3. 执行分页查询
        Page<Order> result = orderMapper.selectPage(page, wrapper);
        // 4. 把每行实体转成列表 DTO（精简字段）
        List<OrderListItemDto> items = result.getRecords().stream()
                .map(this::toListItem)
                .collect(Collectors.toList());
        // 5. 包装成分页结果返回
        return PageResult.of(result.getTotal(), items, query.getPageNum(), query.getPageSize());
    }

    @Override
    public OrderDetailResponseDto detail(Long userId, Long orderId) {
        // 1. 查订单并校验归属：查不到抛 404，不是本人的抛 403
        Order order = getOwnedOrder(userId, orderId);
        // 2. 查该订单的所有明细
        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId));
        // 3. 组装详情 DTO（含地址快照 + 明细列表）返回
        return toDetail(order, items);
    }

    @Override
    @Transactional
    public void cancel(Long userId, Long orderId) {
        // 1. 查订单并校验归属
        Order order = getOwnedOrder(userId, orderId);
        // 2. 只有「待支付」能取消，其他状态一律拒绝
        if (order.getStatus() == null || order.getStatus() != OrderStatus.PENDING_PAYMENT.getCode()) {
            throw OrderErrorCode.ORDER_STATUS_ERROR.exception();
        }
        // 3. 查出全部明细，逐个回补库存
        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId));
        for (OrderItem item : items) {
            restoreStock(item.getSkuId(), item.getQuantity());
        }
        // 4. 把订单状态改成「已取消」
        Order update = new Order();
        update.setId(orderId);
        update.setStatus(OrderStatus.CANCELLED.getCode());
        orderMapper.updateById(update);
    }

    @Override
    public OrderInfoResponse getOrderByOrderNo(String orderNo) {
        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNo, orderNo));
        if (order == null) {
            throw OrderErrorCode.ORDER_NOT_FOUND.exception();
        }
        OrderInfoResponse dto = new OrderInfoResponse();
        dto.setOrderNo(order.getOrderNo());
        dto.setUserId(order.getUserId());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        return dto;
    }

    @Override
    @Transactional
    public void markPaid(String orderNo) {
        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNo, orderNo));
        if (order == null) {
            throw OrderErrorCode.ORDER_NOT_FOUND.exception();
        }
        // 幂等：已是「已支付」直接返回，避免重复推进
        if (order.getStatus() != null && order.getStatus() == OrderStatus.PAID.getCode()) {
            return;
        }
        if (order.getStatus() == null || order.getStatus() != OrderStatus.PENDING_PAYMENT.getCode()) {
            throw OrderErrorCode.ORDER_STATUS_ERROR.exception();
        }
        Order update = new Order();
        update.setId(order.getId());
        update.setStatus(OrderStatus.PAID.getCode());
        orderMapper.updateById(update);
    }

    @Override
    public PageResult<OrderAdminListDto> adminPage(OrderListQueryDto query) {

        Page<Order> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(query.getStatus() != null, Order::getStatus, query.getStatus())
                .eq(query.getOrderNo() != null, Order::getOrderNo, query.getOrderNo())
                .orderByDesc(Order::getId);
        Page<Order> result = orderMapper.selectPage(page, wrapper);
        List<OrderAdminListDto> items = result.getRecords().stream()
                .map(this::toAdminListItem)
                .collect(Collectors.toList());
        return PageResult.of(result.getTotal(), items, query.getPageNum(), query.getPageSize());
    }

    /**
     * 通过 Feign 查询 SKU，把 404 转成业务异常。
     */
    private SkuDetailResponse getSku(Long skuId) {
        try {
            SkuDetailResponse sku = productClient.getSku(skuId).getData();
            if (sku == null) {
                throw OrderErrorCode.SKU_NOT_FOUND.exception();
            }
            return sku;
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw OrderErrorCode.SKU_NOT_FOUND.exception();
            }
            throw OrderErrorCode.PRODUCT_SERVICE_ERROR.exception();
        }
    }

    /**
     * 通过 Feign 扣减库存，把库存不足（409）转成业务异常。
     */
    private void deductStock(Long skuId, Integer quantity) {
        try {
            productClient.deduct(new StockOperateRequest(skuId, quantity));
        } catch (FeignException e) {
            if (e.status() == 409) {
                throw OrderErrorCode.STOCK_NOT_ENOUGH.exception();
            }
            throw OrderErrorCode.PRODUCT_SERVICE_ERROR.exception();
        }
    }

    /**
     * 通过 Feign 回补库存，失败抛异常由后续补偿兜底。
     */
    private void restoreStock(Long skuId, Integer quantity) {
        try {
            productClient.restore(new StockOperateRequest(skuId, quantity));
        } catch (FeignException e) {
            throw new BizException(500, "库存回补失败，请稍后重试");
        }
    }

    /**
     * 组装订单实体，写入地址快照与按 SKU 计算的金额。
     */
    private Order buildOrder(Long userId, CreateOrderRequestDto request, SkuDetailResponse sku) {
        AddressDto address = request.getAddress();
        Order order = new Order();
        order.setOrderNo(String.valueOf(IdWorker.getId()));
        order.setRequestId(request.getRequestId());
        order.setUserId(userId);
        order.setStatus(OrderStatus.PENDING_PAYMENT.getCode());
        order.setTotalAmount(sku.getPrice() * request.getQuantity());
        order.setReceiver(address.getReceiver());
        order.setPhone(address.getPhone());
        order.setProvince(address.getProvince());
        order.setCity(address.getCity());
        order.setDistrict(address.getDistrict());
        order.setDetail(address.getDetail());
        order.setRemark(request.getRemark());
        return order;
    }

    /**
     * 组装订单明细，写入商品与 SKU 快照。
     */
    private OrderItem buildItem(Long orderId, CreateOrderRequestDto request, SkuDetailResponse sku) {
        OrderItem item = new OrderItem();
        item.setOrderId(orderId);
        item.setProductId(sku.getProductId());
        item.setSkuId(sku.getId());
        item.setSkuName(sku.getSkuName());
        item.setImage(sku.getImage());
        item.setPrice(sku.getPrice());
        item.setQuantity(request.getQuantity());
        return item;
    }

    /**
     * 查询订单并校验归属，非本人订单拒绝访问。
     */
    private Order getOwnedOrder(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw OrderErrorCode.ORDER_NOT_FOUND.exception();
        }
        if (!userId.equals(order.getUserId())) {
            throw OrderErrorCode.NO_PERMISSION.exception();
        }
        return order;
    }

    /**
     * 订单实体转列表项 DTO。
     */
    private OrderListItemDto toListItem(Order order) {
        OrderListItemDto dto = new OrderListItemDto();
        dto.setId(order.getId());
        dto.setOrderNo(order.getOrderNo());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setCreatedAt(order.getCreatedAt());
        return dto;
    }

    private OrderAdminListDto toAdminListItem(Order order) {
        OrderAdminListDto dto = new OrderAdminListDto();
        dto.setId(order.getId());
        dto.setOrderNo(order.getOrderNo());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setUserId(order.getUserId());
        dto.setCreatedAt(order.getCreatedAt());
        return dto;
    }

    /**
     * 订单明细实体转明细 DTO。
     */
    private OrderItemResponseDto toItem(OrderItem item) {
        OrderItemResponseDto response = new OrderItemResponseDto();
        response.setProductId(item.getProductId());
        response.setSkuId(item.getSkuId());
        response.setSkuName(item.getSkuName());
        response.setImage(item.getImage());
        response.setPrice(item.getPrice());
        response.setQuantity(item.getQuantity());
        return response;
    }

    /**
     * 订单实体 + 明细列表转详情 DTO。
     */
    private OrderDetailResponseDto toDetail(Order order, List<OrderItem> items) {
        OrderDetailResponseDto dto = new OrderDetailResponseDto();
        dto.setId(order.getId());
        dto.setOrderNo(order.getOrderNo());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setReceiver(order.getReceiver());
        dto.setPhone(order.getPhone());
        dto.setProvince(order.getProvince());
        dto.setCity(order.getCity());
        dto.setDistrict(order.getDistrict());
        dto.setDetail(order.getDetail());
        dto.setRemark(order.getRemark());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setItems(items.stream().map(this::toItem).collect(Collectors.toList()));
        return dto;
    }

}
