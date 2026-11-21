package com.dimbox.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dimbox.user.dto.LoginRequest;
import com.dimbox.user.dto.LoginResponse;
import com.dimbox.user.dto.RegisterRequest;
import com.dimbox.user.dto.UserInfoResponse;
import com.dimbox.user.entity.User;
import com.dimbox.user.enums.UserErrorCode;
import com.dimbox.user.mapper.UserMapper;
import com.dimbox.user.service.AuthService;
import com.dimbox.user.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public void register(RegisterRequest request) {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername()));
        if (count != null && count > 0) {
            throw UserErrorCode.USERNAME_EXISTS.exception();
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname() == null || request.getNickname().isBlank()
                ? request.getUsername() : request.getNickname());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus(1);
        userMapper.insert(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername()));
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw UserErrorCode.LOGIN_FAILED.exception();
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw UserErrorCode.USER_DISABLED.exception();
        }

        String token = jwtUtil.generateToken(user.getId(), user.getRole());
        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .role(user.getRole())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .build();
    }

    @Override
    public UserInfoResponse me(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw UserErrorCode.USER_NOT_FOUND.exception();
        }
        return UserInfoResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .email(user.getEmail())
                .build();
    }
}
