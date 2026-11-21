package com.dimbox.user.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateProfileRequest {

    @NotBlank(message = "昵称不能为空")
    @Size(max = 32, message = "昵称最长 32 字")
    private String nickname;

    @NotNull(message = "性别不能为空")
    @Min(value = 0, message = "性别取值 0/1/2")
    @Max(value = 2, message = "性别取值 0/1/2")
    private Integer gender;

    @Size(max = 255, message = "头像地址过长")
    private String avatar;

    @PastOrPresent(message = "生日不能晚于今天")
    private LocalDate birthday;

    @Size(max = 255, message = "简介最长 255 字")
    private String bio;
}
