package com.dimbox.user.service;

import com.dimbox.user.dto.ChangePasswordRequest;
import com.dimbox.user.dto.UpdateProfileRequest;
import com.dimbox.user.dto.UserProfileResponse;

public interface ProfileService {

    UserProfileResponse getProfile(Long userId);

    void updateProfile(Long userId, UpdateProfileRequest request);

    void changePassword(Long userId, ChangePasswordRequest request);
}
