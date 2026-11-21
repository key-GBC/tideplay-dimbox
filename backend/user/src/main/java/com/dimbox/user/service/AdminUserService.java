package com.dimbox.user.service;

import com.dimbox.common.result.PageResult;
import com.dimbox.user.dto.UpdateStatusRequest;
import com.dimbox.user.dto.UserAdminListResponse;
import com.dimbox.user.dto.UserListQueryRequest;

public interface AdminUserService {

    PageResult<UserAdminListResponse> list(UserListQueryRequest query);

    void updateStatus(Long userId, UpdateStatusRequest request);
}
