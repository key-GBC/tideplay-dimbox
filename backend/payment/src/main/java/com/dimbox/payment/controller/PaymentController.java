package com.dimbox.payment.controller;

import com.dimbox.common.exception.BizException;
import com.dimbox.common.result.ErrorCode;
import com.dimbox.common.result.Result;
import com.dimbox.payment.dto.CreatePaymentRequestDto;
import com.dimbox.payment.dto.PaymentCallbackRequestDto;
import com.dimbox.payment.dto.PaymentResponseDto;
import com.dimbox.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    public Result<PaymentResponseDto> create(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                             @Valid @RequestBody CreatePaymentRequestDto request) {
        checkUser(userId);
        return Result.success(paymentService.create(userId, request));
    }

    @PostMapping("/{paymentNo}/pay")
    public Result<PaymentResponseDto> pay(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                          @PathVariable("paymentNo") String paymentNo) {
        checkUser(userId);
        return Result.success(paymentService.pay(userId, paymentNo));
    }

    @PostMapping("/callback")
    public Result<Void> callback(@Valid @RequestBody PaymentCallbackRequestDto request) {
        paymentService.callback(request);
        return Result.success();
    }

    @GetMapping("/{paymentNo}")
    public Result<PaymentResponseDto> query(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                             @PathVariable("paymentNo") String paymentNo) {
        checkUser(userId);
        return Result.success(paymentService.query(userId, paymentNo));
    }

    private void checkUser(Long userId) {
        if(userId == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED);
        }
    }
}
