package com.dimbox.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.dimbox.common.exception.BizException;
import com.dimbox.payment.client.OrderClient;
import com.dimbox.payment.client.OrderInfoResponse;
import com.dimbox.payment.dto.CreatePaymentRequestDto;
import com.dimbox.payment.dto.PaymentCallbackRequestDto;
import com.dimbox.payment.dto.PaymentResponseDto;
import com.dimbox.payment.entity.Payment;
import com.dimbox.payment.enums.PaymentErrorCode;
import com.dimbox.payment.enums.PaymentStatus;
import com.dimbox.payment.mapper.PaymentMapper;
import com.dimbox.payment.service.PaymentService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final int ORDER_STATUS_PENDING_PAYMENT = 10;

    private final PaymentMapper paymentMapper;
    private final OrderClient orderClient;

    @Override
    @Transactional
    public PaymentResponseDto create(Long userId, CreatePaymentRequestDto request) {

        OrderInfoResponse order = getOrder(request.getOrderNo());

        if(!userId.equals(order.getUserId())) {
            throw PaymentErrorCode.NO_PERMISSION.exception();
        }

        if(order.getStatus() == null || order.getStatus() != ORDER_STATUS_PENDING_PAYMENT) {
            throw PaymentErrorCode.ORDER_STATUS_ERROR.exception();
        }

        Payment existing = paymentMapper.selectOne(new LambdaQueryWrapper<Payment>()
                .eq(Payment::getOrderNo, request.getOrderNo()));
        if(existing != null) {
            return toResponse(existing);
        }
        Payment payment = new Payment();
        payment.setPaymentNo(String.valueOf(IdWorker.getId()));
        payment.setOrderNo(order.getOrderNo());
        payment.setUserId(userId);
        payment.setAmount(order.getTotalAmount());
        payment.setChannel("mock");
        payment.setStatus(PaymentStatus.PENDING.getCode());
        try {
            paymentMapper.insert(payment);
        } catch (DuplicateKeyException e) {
            Payment duplicate = paymentMapper.selectOne(new LambdaQueryWrapper<Payment>()
                    .eq(Payment::getOrderNo, request.getOrderNo()));
            if (duplicate != null) {
                return toResponse(duplicate);
            }
                throw e;
        }
        return toResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponseDto pay(Long userId, String paymentNo) {

        Payment payment = getOwnedPayment(userId, paymentNo);
        markPaid(payment);
        return toResponse(payment);
    }

    @Override
    @Transactional
    public void callback(PaymentCallbackRequestDto request) {

        Payment payment = paymentMapper.selectOne(new LambdaQueryWrapper<Payment>()
                .eq(Payment::getPaymentNo, request.getPaymentNo()));
        if(payment == null) {
            throw PaymentErrorCode.PAYMENT_NOT_FOUND.exception();
        }
        markPaid(payment);
    }

    @Override
    public PaymentResponseDto query(Long userId, String paymentNo) {
        Payment payment = getOwnedPayment(userId, paymentNo);
        return toResponse(payment);
    }

    private void markPaid(Payment payment) {

        if(payment.getStatus() != null && payment.getStatus() == PaymentStatus.PAID.getCode()) {
            return;
        }
        markOrderPaid(payment.getOrderNo());

        LocalDateTime now = LocalDateTime.now();
        Payment update = new Payment();
        update.setId(payment.getId());
        update.setStatus(PaymentStatus.PAID.getCode());
        update.setPaidAt(now);
        paymentMapper.updateById(update);

        payment.setStatus(PaymentStatus.PAID.getCode());
        payment.setPaidAt(now);
    }

    private void markOrderPaid(String orderNo) {
        try {
            orderClient.markPaid(orderNo);
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw PaymentErrorCode.ORDER_NOT_FOUND.exception();
            }
            if (e.status() == 409) {
                throw PaymentErrorCode.ORDER_STATUS_ERROR.exception();
            }
            throw new BizException(500, "订单服务调用失败");
        }
    }

    private OrderInfoResponse getOrder(String orderNo) {
        try {
            OrderInfoResponse order = orderClient.getOrderByNo(orderNo).getData();
            if (order == null) {
                throw PaymentErrorCode.ORDER_NOT_FOUND.exception();
            }
            return order;
        } catch(FeignException e) {
            if(e.status() == 404) {
                throw PaymentErrorCode.ORDER_NOT_FOUND.exception();
            }
            throw new BizException(500, "订单服务调用失败");
        }
    }

    private Payment getOwnedPayment(Long userId, String paymentNo) {
        Payment payment = paymentMapper.selectOne(new LambdaQueryWrapper<Payment>()
                .eq(Payment::getPaymentNo, paymentNo));
        if(payment == null) {
            throw PaymentErrorCode.PAYMENT_NOT_FOUND.exception();
        }
        if(!userId.equals(payment.getUserId())) {
            throw PaymentErrorCode.NO_PERMISSION.exception();
        }
        return payment;
    }

    private PaymentResponseDto toResponse(Payment payment) {
        PaymentResponseDto dto = new PaymentResponseDto();
        dto.setPaymentNo(payment.getPaymentNo());
        dto.setOrderNo(payment.getOrderNo());
        dto.setAmount(payment.getAmount());
        dto.setChannel(payment.getChannel());
        dto.setStatus(payment.getStatus());
        dto.setPaidAt(payment.getPaidAt());
        return dto;
    }

}
