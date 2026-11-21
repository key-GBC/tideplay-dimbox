package com.dimbox.common.result;

import lombok.Getter;

/**
 * 全局错误码，与 HTTP 状态码保持一致：200 成功，4xx 客户端错误，5xx 服务端错误。
 */
@Getter
public enum ErrorCode {

    SUCCESS(200, "成功"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方式不支持"),
    CONFLICT(409, "资源冲突"),
    RATE_LIMITED(429, "请求过于频繁"),
    SYSTEM_ERROR(500, "系统繁忙，请稍后重试");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
