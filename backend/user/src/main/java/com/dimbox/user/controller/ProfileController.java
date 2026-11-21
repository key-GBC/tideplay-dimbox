package com.dimbox.user.controller;

import com.dimbox.common.exception.BizException;
import com.dimbox.common.result.ErrorCode;
import com.dimbox.common.result.Result;
import com.dimbox.user.dto.ChangePasswordRequest;
import com.dimbox.user.dto.UpdateProfileRequest;
import com.dimbox.user.dto.UserProfileResponse;
import com.dimbox.user.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/profile")
    public Result<UserProfileResponse> profile(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        requireUser(userId);
        return Result.success(profileService.getProfile(userId));
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                      @Valid @RequestBody UpdateProfileRequest request) {
        requireUser(userId);
        profileService.updateProfile(userId, request);
        return Result.success();
    }

    @PutMapping("/password")
    public Result<Void> changePassword(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                       @Valid @RequestBody ChangePasswordRequest request) {
        requireUser(userId);
        profileService.changePassword(userId, request);
        return Result.success();
    }

    private void requireUser(Long userId) {
        if (userId == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED);
        }
    }
}
