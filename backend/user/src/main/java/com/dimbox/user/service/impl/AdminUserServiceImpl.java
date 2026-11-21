package com.dimbox.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dimbox.common.result.PageResult;
import com.dimbox.user.dto.UpdateStatusRequest;
import com.dimbox.user.dto.UserAdminListResponse;
import com.dimbox.user.dto.UserListQueryRequest;
import com.dimbox.user.entity.User;
import com.dimbox.user.enums.UserErrorCode;
import com.dimbox.user.mapper.UserMapper;
import com.dimbox.user.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserMapper userMapper;

    @Override
    public PageResult<UserAdminListResponse> list(UserListQueryRequest query) {

        Page<User> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .eq(query.getStatus() != null, User::getStatus, query.getStatus())
                .eq(query.getRole() != null, User::getRole, query.getRole())
                .like(StringUtils.hasText(query.getUsername()), User::getUsername, query.getUsername())
                .orderByDesc(User::getCreatedAt);

        Page<User> result = userMapper.selectPage(page, wrapper);
        List<UserAdminListResponse> items = result.getRecords().stream()
                .map(this::toUserList)
                .collect(Collectors.toList());

        return PageResult.of(result.getTotal(), items, query.getPageNum(), query.getPageSize());
    }

    @Override
    public void updateStatus(Long userId, UpdateStatusRequest request) {
        if (userMapper.selectById(userId) == null) {
            throw UserErrorCode.USER_NOT_FOUND.exception();
        }
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getStatus, request.getStatus());
        userMapper.update(null, wrapper);
    }

    private UserAdminListResponse toUserList(User user) {
        UserAdminListResponse response = new UserAdminListResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());
        response.setStatus(user.getStatus());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}
