package com.dimbox.user.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserAdminListResponse {

    private Long id;

    private String username;

    private String nickname;

    private String phone;

    private Integer role;

    private Integer status;

    private LocalDateTime createdAt;
}
