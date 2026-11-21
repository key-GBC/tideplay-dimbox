package com.dimbox.user.controller;

import com.dimbox.common.result.Result;
import com.dimbox.common.exception.BizException;
import com.dimbox.common.result.ErrorCode;
import com.dimbox.user.dto.LoginRequest;
import com.dimbox.user.dto.LoginResponse;
import com.dimbox.user.dto.RegisterRequest;
import com.dimbox.user.dto.UserInfoResponse;
import com.dimbox.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    @GetMapping("/me")
    public Result<UserInfoResponse> me(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED);
        }
        return Result.success(authService.me(userId));
    }
}
