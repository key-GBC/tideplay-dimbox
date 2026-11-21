package com.dimbox.common.exception;

import com.dimbox.common.result.ErrorCode;

/**
 * 断言工具：条件不满足时抛出 BizException。
 *
 * <p>此类提供了一系列静态方法，用于在程序运行时检查条件是否满足。
 * 如果条件不满足，则会抛出带有指定错误码和消息的 BizException 异常。
 * 这种断言机制常用于业务逻辑验证，确保程序的健壮性和数据的正确性。</p>
 */
public final class BizAssert {

    /**
     * 私有构造方法，防止实例化此类。
     *
     * <p>由于此类只提供静态方法，因此不需要创建实例。
     * 将构造方法设为私有可以确保类的不可实例化特性。</p>
     */
    private BizAssert() {
    }

    /**
     * 检查条件是否为真，如果为假则抛出异常。
     *
     * @param expression 要检查的表达式
     * @param errorCode 错误码，用于标识具体的错误类型
     * @throws BizException 当表达式为 false 时抛出
     */
    public static void isTrue(boolean expression, ErrorCode errorCode) {
        if (!expression) {
            throw new BizException(errorCode);
        }
    }

    /**
     * 检查条件是否为真，如果为假则抛出带有自定义消息的异常。
     *
     * @param expression 要检查的表达式
     * @param errorCode 错误码，用于标识具体的错误类型
     * @param message 异常消息，用于提供更详细的错误信息
     * @throws BizException 当表达式为 false 时抛出
     */
    public static void isTrue(boolean expression, ErrorCode errorCode, String message) {
        if (!expression) {
            throw new BizException(errorCode, message);
        }
    }

    /**
     * 检查对象是否为非空，如果为空则抛出异常。
     *
     * @param obj 要检查的对象
     * @param errorCode 错误码，用于标识具体的错误类型
     * @throws BizException 当对象为 null 时抛出
     */
    public static void notNull(Object obj, ErrorCode errorCode) {
        isTrue(obj != null, errorCode);
    }

    /**
     * 检查对象是否为非空，如果为空则抛出带有自定义消息的异常。
     *
     * @param obj 要检查的对象
     * @param errorCode 错误码，用于标识具体的错误类型
     * @param message 异常消息，用于提供更详细的错误信息
     * @throws BizException 当对象为 null 时抛出
     */
    public static void notNull(Object obj, ErrorCode errorCode, String message) {
        isTrue(obj != null, errorCode, message);
    }

    /**
     * 检查字符串是否为非空且非空白，如果为空或空白则抛出异常。
     *
     * @param text 要检查的字符串
     * @param errorCode 错误码，用于标识具体的错误类型
     * @throws BizException 当字符串为 null 或空白时抛出
     */
    public static void notBlank(String text, ErrorCode errorCode) {
        isTrue(text != null && !text.trim().isEmpty(), errorCode);
    }
}
