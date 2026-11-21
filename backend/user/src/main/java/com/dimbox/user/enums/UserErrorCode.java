package com.dimbox.user.enums;

import com.dimbox.common.exception.BizException;
import lombok.Getter;

/**
 * user 服务私有错误码，取值与 HTTP 状态码一致，用 message 区分具体业务含义。
 */
@Getter
public enum UserErrorCode {

    USERNAME_EXISTS(409, "用户名已存在"),
    LOGIN_FAILED(400, "用户名或密码错误"),
    USER_DISABLED(403, "账号已被禁用"),
    USER_NOT_FOUND(404, "用户不存在"),
    OLD_PASSWORD_ERROR(400, "旧密码错误"),
    PASSWORD_SAME(400, "新密码不能与旧密码相同"),
    ADDRESS_LIMIT_EXCEEDED(400, "地址数量超限"),
    ADDRESS_NOT_FOUND(404, "地址不存在"),
    NO_PERMISSION(403, "无权操作该地址");

    private final int code;
    private final String message;

    UserErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public BizException exception() {
        return new BizException(code, message);
    }
}
