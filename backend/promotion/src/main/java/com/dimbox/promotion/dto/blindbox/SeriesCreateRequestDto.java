package com.dimbox.promotion.dto.blindbox;

import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class SeriesCreateRequestDto {

    @NotBlank(message = "系列名不能为空")
    @Size(max = 64, message = "系列名最多64个字符")
    private String name;

    @Size(max = 500, message = "描述最多500个字符")
    private String description;

    private String coverImage;

    @NotNull(message = "价格不能为空")
    @Min(value = 0, message = "价格不能小于0")
    private Integer price;

}
