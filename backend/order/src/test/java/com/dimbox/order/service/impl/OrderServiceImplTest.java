package com.dimbox.order.service.impl;

import com.dimbox.common.exception.BizException;
import com.dimbox.common.result.Result;
import com.dimbox.order.client.ProductClient;
import com.dimbox.order.client.SkuDetailResponse;
import com.dimbox.order.client.StockOperateRequest;
import com.dimbox.order.dto.AddressDto;
import com.dimbox.order.dto.CreateOrderRequestDto;
import com.dimbox.order.entity.Order;
import com.dimbox.order.mapper.OrderItemMapper;
import com.dimbox.order.mapper.OrderMapper;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderItemMapper orderItemMapper;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private OrderServiceImpl orderService;

    private CreateOrderRequestDto request;
    private SkuDetailResponse sku;

    @BeforeEach
    void setUp() {
        AddressDto address = new AddressDto();
        address.setReceiver("张三");
        address.setPhone("13800138000");
        address.setProvince("广东省");
        address.setCity("深圳市");
        address.setDistrict("南山区");
        address.setDetail("科技园 1 号");

        request = new CreateOrderRequestDto();
        request.setRequestId("req-1");
        request.setSkuId(1001L);
        request.setQuantity(2);
        request.setAddress(address);

        sku = new SkuDetailResponse();
        sku.setId(1001L);
        sku.setProductId(100L);
        sku.setSkuName("标准款");
        sku.setPrice(6900);
        sku.setStock(10);
        sku.setStatus(1);
    }

    @Test
    void createOrder_成功时扣库存并返回订单号() {
        when(orderMapper.selectOne(any())).thenReturn(null);
        when(productClient.getSku(1001L)).thenReturn(Result.success(sku));
        when(productClient.deduct(any(StockOperateRequest.class))).thenReturn(Result.success(null));

        String orderNo = orderService.createOrder(1L, request);

        assertThat(orderNo).isNotBlank();
        verify(productClient).deduct(new StockOperateRequest(1001L, 2));
    }

    @Test
    void createOrder_重复requestId时不扣库存直接返回原订单() {
        Order existing = new Order();
        existing.setOrderNo("202610270001");

        when(orderMapper.selectOne(any())).thenReturn(existing);

        String orderNo = orderService.createOrder(1L, request);

        assertThat(orderNo).isEqualTo("202610270001");
        verify(productClient, never()).deduct(any());
    }

    @Test
    void createOrder_库存不足时抛业务异常() {
        FeignException feignException = mock(FeignException.class);
        when(feignException.status()).thenReturn(409);

        when(orderMapper.selectOne(any())).thenReturn(null);
        when(productClient.getSku(1001L)).thenReturn(Result.success(sku));
        when(productClient.deduct(any(StockOperateRequest.class))).thenThrow(feignException);

        assertThatThrownBy(() -> orderService.createOrder(1L, request))
                .isInstanceOf(BizException.class)
                .hasMessage("库存不足");
    }
}
