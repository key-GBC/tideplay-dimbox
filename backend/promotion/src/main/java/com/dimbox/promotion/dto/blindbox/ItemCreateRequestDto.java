package com.dimbox.promotion.dto.blindbox;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ItemCreateRequestDto {

    @NotNull(message = "SKU不能为空")
    private Long skuId;

    @NotBlank(message = "样式名称不能为空")
    @Size(max = 64, message = "样式名称最多64个字符")
    private String styleName;

    private String image;

    @NotNull(message = "概率不能为空")
    @Min(value = 0, message = "概率不能小于0")
    @Max(value = 10000, message = "概率不能大于10000")
    private Integer probability;

    @NotNull(message = "是否隐藏款不能为空")
    @Min(value = 0, message = "是否隐藏款取值0/1")
    @Max(value = 1, message = "是否隐藏款取值0/1")
    private Integer isHidden;
}
