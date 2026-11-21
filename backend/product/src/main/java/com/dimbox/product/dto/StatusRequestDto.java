package com.dimbox.product.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 商品上下架请求参数（管理端 → 后端）。
 */
@Data
public class StatusRequestDto {

    /** 状态：1 上架 / 0 下架 */
    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态不能小于0")
    @Max(value = 1, message = "状态不能大于1")
    private Integer status;
}
