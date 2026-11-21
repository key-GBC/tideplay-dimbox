package com.dimbox.common.result;

import lombok.Getter;

/**
 * 统一响应结构。code 与 HTTP 状态码一致，200 表示成功，见 {@link ErrorCode}。
 * 这是一个泛型类，用于封装API的响应结果，包含状态码、消息和数据三部分。
 */
@Getter
public class Result<T> {

    /**
     * 响应状态码，0表示成功，非0表示失败，具体错误码见ErrorCode枚举类
     */
    private final int code;
    /**
     * 响应消息，用于描述响应状态或错误信息
     */
    private final String message;
    /**
     * 响应数据，泛型类型，可以承载任意类型的数据
     */
    private final T data;

    /**
     * 私有构造方法，用于创建Result实例
     * @param code 响应状态码
     * @param message 响应消息
     * @param data 响应数据
     */
    private Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 创建一个成功的响应，不带数据
     * @param <T> 数据类型
     * @return 返回一个成功的Result实例
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 创建一个成功的响应，带数据
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 返回一个带数据的成功Result实例
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), data);
    }

    /**
     * 创建一个失败的响应，使用预定义的错误码
     * @param errorCode 错误码枚举
     * @param <T> 数据类型
     * @return 返回一个失败的Result实例
     */
    public static <T> Result<T> fail(ErrorCode errorCode) {
        return new Result<>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    /**
     * 创建一个失败的响应，使用自定义的错误码和消息
     * @param code 错误码
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 返回一个失败的Result实例
     */
    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null);
    }
}
