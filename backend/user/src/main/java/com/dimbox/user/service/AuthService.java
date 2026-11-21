package com.dimbox.user.service;

import com.dimbox.user.dto.LoginRequest;
import com.dimbox.user.dto.LoginResponse;
import com.dimbox.user.dto.RegisterRequest;
import com.dimbox.user.dto.UserInfoResponse;

public interface AuthService {

    void register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    UserInfoResponse me(Long userId);
}
