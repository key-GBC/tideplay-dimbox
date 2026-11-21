package com.dimbox.payment.service;

import com.dimbox.payment.dto.CreatePaymentRequestDto;
import com.dimbox.payment.dto.PaymentCallbackRequestDto;
import com.dimbox.payment.dto.PaymentResponseDto;

public interface PaymentService {

    PaymentResponseDto create(Long userId, CreatePaymentRequestDto request);

    PaymentResponseDto pay(Long userId, String paymentNo);

    void callback(PaymentCallbackRequestDto request);

    PaymentResponseDto query(Long userId, String paymentNo);
}
