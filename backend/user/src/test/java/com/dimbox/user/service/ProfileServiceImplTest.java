package com.dimbox.user.service;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.dimbox.common.exception.BizException;
import com.dimbox.user.dto.ChangePasswordRequest;
import com.dimbox.user.dto.UpdateProfileRequest;
import com.dimbox.user.dto.UserProfileResponse;
import com.dimbox.user.entity.User;
import com.dimbox.user.mapper.UserMapper;
import com.dimbox.user.service.impl.ProfileServiceImpl;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ProfileServiceImpl profileService;

    @BeforeAll
    static void initTableInfo() {
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(new MybatisConfiguration(), "");
        TableInfoHelper.initTableInfo(assistant, User.class);
    }

    @Test
    void getProfile_whenUserNotFound_throwsNotFound() {
        when(userMapper.selectById(1L)).thenReturn(null);

        BizException ex = assertThrows(BizException.class, () -> profileService.getProfile(1L));

        assertEquals(404, ex.getCode());
    }

    @Test
    void getProfile_whenUserExists_returnsProfile() {
        when(userMapper.selectById(1L)).thenReturn(buildUser());

        UserProfileResponse response = profileService.getProfile(1L);

        assertEquals("test", response.getUsername());
        assertEquals("测试用户", response.getNickname());
    }

    @Test
    void updateProfile_whenUserNotFound_throwsNotFound() {
        when(userMapper.selectById(1L)).thenReturn(null);
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setNickname("新昵称");
        request.setGender(1);

        BizException ex = assertThrows(BizException.class, () -> profileService.updateProfile(1L, request));

        assertEquals(404, ex.getCode());
    }

    @Test
    void changePassword_whenOldPasswordWrong_throws() {
        when(userMapper.selectById(1L)).thenReturn(buildUser());
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("wrong");
        request.setNewPassword("newPass123");

        BizException ex = assertThrows(BizException.class, () -> profileService.changePassword(1L, request));

        assertEquals(400, ex.getCode());
    }

    @Test
    void changePassword_whenSameAsOld_throws() {
        when(userMapper.selectById(1L)).thenReturn(buildUser());
        when(passwordEncoder.matches("oldPass123", "encoded")).thenReturn(true);

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPass123");
        request.setNewPassword("oldPass123");

        BizException ex = assertThrows(BizException.class, () -> profileService.changePassword(1L, request));

        assertEquals(400, ex.getCode());
    }

    @Test
    void changePassword_whenValid_updatesPassword() {
        when(userMapper.selectById(1L)).thenReturn(buildUser());
        when(passwordEncoder.matches("oldPass123", "encoded")).thenReturn(true);
        when(passwordEncoder.encode("newPass123")).thenReturn("encodedNew");

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPass123");
        request.setNewPassword("newPass123");

        profileService.changePassword(1L, request);

        verify(userMapper).update(any(), any());
    }

    private User buildUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setNickname("测试用户");
        user.setPassword("encoded");
        return user;
    }
}
