package com.dimbox.product.enums;

import com.dimbox.common.exception.BizException;
import lombok.Getter;

@Getter
public enum ProductErrorCode {

    CATEGORY_NOT_FOUND(404, "分类不存在"),
    CATEGORY_HAS_CHILDREN(409, "分类下存在子分类，无法删除"),
    BRAND_NOT_FOUND(404, "品牌不存在"),
    PRODUCT_NOT_FOUND(404, "商品不存在"),
    SKU_NOT_FOUND(404, "款式不存在"),
    SKU_LAST_ONE(409, "商品至少保留一个款式"),
    STOCK_NOT_ENOUGH(409, "库存不足");

    private final int code;
    private final String message;

    ProductErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public BizException exception() {
        return new BizException(code, message);
    }
}
