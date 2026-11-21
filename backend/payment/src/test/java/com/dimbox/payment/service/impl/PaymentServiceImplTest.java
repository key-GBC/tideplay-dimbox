package com.dimbox.payment.service.impl;

import com.dimbox.common.result.Result;
import com.dimbox.payment.client.OrderClient;
import com.dimbox.payment.client.OrderInfoResponse;
import com.dimbox.payment.dto.CreatePaymentRequestDto;
import com.dimbox.payment.dto.PaymentCallbackRequestDto;
import com.dimbox.payment.dto.PaymentResponseDto;
import com.dimbox.payment.entity.Payment;
import com.dimbox.payment.enums.PaymentStatus;
import com.dimbox.payment.mapper.PaymentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private OrderClient orderClient;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private OrderInfoResponse order;
    private CreatePaymentRequestDto request;

    @BeforeEach
    void setUp() {
        order = new OrderInfoResponse();
        order.setOrderNo("202611030001");
        order.setUserId(1L);
        order.setStatus(10);
        order.setTotalAmount(13800);

        request = new CreatePaymentRequestDto();
        request.setOrderNo("202611030001");
    }

    @Test
    void create_创建支付单成功() {
        when(orderClient.getOrderByNo("202611030001")).thenReturn(Result.success(order));
        when(paymentMapper.selectOne(any())).thenReturn(null);

        PaymentResponseDto dto = paymentService.create(1L, request);

        assertThat(dto.getOrderNo()).isEqualTo("202611030001");
        assertThat(dto.getAmount()).isEqualTo(13800);
        assertThat(dto.getStatus()).isEqualTo(PaymentStatus.PENDING.getCode());
        verify(paymentMapper).insert(any(Payment.class));
    }

    @Test
    void create_已存在支付单时直接返回不重复插入() {
        Payment existing = new Payment();
        existing.setPaymentNo("P1");
        existing.setOrderNo("202611030001");
        existing.setUserId(1L);
        existing.setAmount(13800);
        existing.setStatus(PaymentStatus.PENDING.getCode());

        when(orderClient.getOrderByNo("202611030001")).thenReturn(Result.success(order));
        when(paymentMapper.selectOne(any())).thenReturn(existing);

        PaymentResponseDto dto = paymentService.create(1L, request);

        assertThat(dto.getPaymentNo()).isEqualTo("P1");
        verify(paymentMapper, never()).insert(any(Payment.class));
    }

    @Test
    void callback_已支付时幂等不再通知订单() {
        Payment paid = new Payment();
        paid.setId(100L);
        paid.setPaymentNo("P1");
        paid.setOrderNo("202611030001");
        paid.setUserId(1L);
        paid.setAmount(13800);
        paid.setStatus(PaymentStatus.PAID.getCode());
        paid.setPaidAt(LocalDateTime.now());

        when(paymentMapper.selectOne(any())).thenReturn(paid);

        PaymentCallbackRequestDto callback = new PaymentCallbackRequestDto();
        callback.setPaymentNo("P1");
        callback.setChannel("mock");

        paymentService.callback(callback);

        verify(orderClient, never()).markPaid(any());
    }

    @Test
    void callback_待支付时通知订单并推进状态() {
        Payment pending = new Payment();
        pending.setId(100L);
        pending.setPaymentNo("P1");
        pending.setOrderNo("202611030001");
        pending.setUserId(1L);
        pending.setAmount(13800);
        pending.setStatus(PaymentStatus.PENDING.getCode());

        when(paymentMapper.selectOne(any())).thenReturn(pending);
        when(orderClient.markPaid("202611030001")).thenReturn(Result.success());

        PaymentCallbackRequestDto callback = new PaymentCallbackRequestDto();
        callback.setPaymentNo("P1");
        callback.setChannel("mock");

        paymentService.callback(callback);

        verify(orderClient).markPaid("202611030001");
        verify(paymentMapper).updateById(any(Payment.class));
    }
}
