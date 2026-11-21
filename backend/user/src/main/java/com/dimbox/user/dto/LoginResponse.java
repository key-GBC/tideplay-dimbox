package com.dimbox.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

    private String token;

    private Long userId;

    private Integer role;

    private String username;

    private String nickname;
}
