package com.dimbox.promotion.enums;

import com.dimbox.common.exception.BizException;
import lombok.Getter;

/**
 * 秒杀业务错误码，取值与 HTTP 状态码一致。
 */
@Getter
public enum PromotionErrorCode {

    SECKILL_ACTIVITY_NOT_FOUND(404, "秒杀活动不存在"),
    ALREADY_GRABBED(409, "已抢购"),
    STOCK_NOT_ENOUGH(409, "库存不足"),
    SECKILL_NOT_STARTED(409, "活动未开始"),
    SECKILL_ENDED(409, "活动已结束"),
    RATE_LIMITED(429, "请求过于频繁");

    private final int code;
    private final String message;

    PromotionErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public BizException exception() {
        return new BizException(code, message);
    }
}
