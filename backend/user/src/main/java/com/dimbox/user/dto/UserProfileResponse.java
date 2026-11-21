package com.dimbox.user.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserProfileResponse {

    private Long id;

    private String username;

    private String nickname;

    private String avatar;

    private Integer gender;

    private LocalDate birthday;

    private String bio;

    private String phone;

    private String email;
}
