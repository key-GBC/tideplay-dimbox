package com.dimbox.user.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusRequest {

    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态取值 0/1")
    @Max(value = 1, message = "状态取值 0/1")
    private Integer status;
}
