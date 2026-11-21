package com.dimbox.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dimbox.user.dto.ChangePasswordRequest;
import com.dimbox.user.dto.UpdateProfileRequest;
import com.dimbox.user.dto.UserProfileResponse;
import com.dimbox.user.entity.User;
import com.dimbox.user.enums.UserErrorCode;
import com.dimbox.user.mapper.UserMapper;
import com.dimbox.user.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserProfileResponse getProfile(Long userId) {
        return toResponse(getUser(userId));
    }

    @Override
    public void updateProfile(Long userId, UpdateProfileRequest request) {
        getUser(userId);
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getNickname, request.getNickname())
                .set(User::getGender, request.getGender())
                .set(User::getAvatar, request.getAvatar())
                .set(User::getBirthday, request.getBirthday())
                .set(User::getBio, request.getBio());
        userMapper.update(null, wrapper);
    }

    @Override
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = getUser(userId);
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw UserErrorCode.OLD_PASSWORD_ERROR.exception();
        }
        if (request.getNewPassword().equals(request.getOldPassword())) {
            throw UserErrorCode.PASSWORD_SAME.exception();
        }
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getPassword, passwordEncoder.encode(request.getNewPassword()));
        userMapper.update(null, wrapper);
    }

    private User getUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw UserErrorCode.USER_NOT_FOUND.exception();
        }
        return user;
    }

    private UserProfileResponse toResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .gender(user.getGender())
                .birthday(user.getBirthday())
                .bio(user.getBio())
                .phone(user.getPhone())
                .email(user.getEmail())
                .build();
    }
}
