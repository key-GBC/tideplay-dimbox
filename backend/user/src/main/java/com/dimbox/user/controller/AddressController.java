package com.dimbox.user.controller;

import com.dimbox.common.exception.BizException;
import com.dimbox.common.result.ErrorCode;
import com.dimbox.common.result.Result;
import com.dimbox.user.dto.AddressRequest;
import com.dimbox.user.dto.AddressResponse;
import com.dimbox.user.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public Result<List<AddressResponse>> list(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        requireUser(userId);
        return Result.success(addressService.list(userId));
    }

    @PostMapping
    public Result<Long> add(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                            @Valid @RequestBody AddressRequest request) {
        requireUser(userId);
        return Result.success(addressService.add(userId, request));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                               @PathVariable("id") Long addressId,
                               @Valid @RequestBody AddressRequest request) {
        requireUser(userId);
        addressService.update(userId, addressId, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                               @PathVariable("id") Long addressId) {
        requireUser(userId);
        addressService.delete(userId, addressId);
        return Result.success();
    }

    @PutMapping("/{id}/default")
    public Result<Void> setDefault(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                   @PathVariable("id") Long addressId) {
        requireUser(userId);
        addressService.setDefault(userId, addressId);
        return Result.success();
    }

    private void requireUser(Long userId) {
        if (userId == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED);
        }
    }
}
