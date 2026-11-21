package com.dimbox.user.controller;

import com.dimbox.common.result.PageResult;
import com.dimbox.common.result.Result;
import com.dimbox.user.dto.UpdateStatusRequest;
import com.dimbox.user.dto.UserAdminListResponse;
import com.dimbox.user.dto.UserListQueryRequest;
import com.dimbox.user.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/admin")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping("/list")
    public Result<PageResult<UserAdminListResponse>> list(UserListQueryRequest query) {
        return Result.success(adminUserService.list(query));
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable("id") Long userId,
                                     @Valid @RequestBody UpdateStatusRequest request) {
        adminUserService.updateStatus(userId, request);
        return Result.success();
    }
}
