package com.dimbox.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dimbox.user.dto.AddressRequest;
import com.dimbox.user.dto.AddressResponse;
import com.dimbox.user.entity.UserAddress;
import com.dimbox.user.enums.UserErrorCode;
import com.dimbox.user.mapper.UserAddressMapper;
import com.dimbox.user.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private static final int MAX_ADDRESS = 20;

    private final UserAddressMapper userAddressMapper;

    @Override
    public List<AddressResponse> list(Long userId) {
        List<UserAddress> addresses = userAddressMapper.selectList(
                new LambdaQueryWrapper<UserAddress>()
                        .eq(UserAddress::getUserId, userId)
                        .orderByDesc(UserAddress::getIsDefault)
                        .orderByDesc(UserAddress::getId));
        return addresses.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long add(Long userId, AddressRequest request) {
        long count = userAddressMapper.selectCount(
                new LambdaQueryWrapper<UserAddress>().eq(UserAddress::getUserId, userId));
        if (count >= MAX_ADDRESS) {
            throw UserErrorCode.ADDRESS_LIMIT_EXCEEDED.exception();
        }
        if (request.getIsDefault() != null && request.getIsDefault() == 1) {
            clearDefault(userId);
        }

        UserAddress address = new UserAddress();
        address.setUserId(userId);
        address.setReceiver(request.getReceiver());
        address.setPhone(request.getPhone());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setDetail(request.getDetail());
        address.setIsDefault(request.getIsDefault() == null ? 0 : request.getIsDefault());
        userAddressMapper.insert(address);
        return address.getId();
    }

    @Override
    public void update(Long userId, Long addressId, AddressRequest request) {
        getOwnedAddress(userId, addressId);
        LambdaUpdateWrapper<UserAddress> wrapper = new LambdaUpdateWrapper<UserAddress>()
                .eq(UserAddress::getId, addressId)
                .set(UserAddress::getReceiver, request.getReceiver())
                .set(UserAddress::getPhone, request.getPhone())
                .set(UserAddress::getProvince, request.getProvince())
                .set(UserAddress::getCity, request.getCity())
                .set(UserAddress::getDistrict, request.getDistrict())
                .set(UserAddress::getDetail, request.getDetail());
        userAddressMapper.update(null, wrapper);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long addressId) {
        UserAddress address = getOwnedAddress(userId, addressId);
        userAddressMapper.deleteById(addressId);
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            promoteLatestToDefault(userId);
        }
    }

    @Override
    @Transactional
    public void setDefault(Long userId, Long addressId) {
        getOwnedAddress(userId, addressId);
        clearDefault(userId);
        UserAddress target = new UserAddress();
        target.setId(addressId);
        target.setIsDefault(1);
        userAddressMapper.updateById(target);
    }

    private UserAddress getOwnedAddress(Long userId, Long addressId) {
        UserAddress address = userAddressMapper.selectById(addressId);
        if (address == null) {
            throw UserErrorCode.ADDRESS_NOT_FOUND.exception();
        }
        if (!userId.equals(address.getUserId())) {
            throw UserErrorCode.NO_PERMISSION.exception();
        }
        return address;
    }

    private void clearDefault(Long userId) {
        UserAddress clear = new UserAddress();
        clear.setIsDefault(0);
        userAddressMapper.update(clear, new LambdaUpdateWrapper<UserAddress>()
                .eq(UserAddress::getUserId, userId)
                .eq(UserAddress::getIsDefault, 1));
    }

    private void promoteLatestToDefault(Long userId) {
        List<UserAddress> list = userAddressMapper.selectList(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getUserId, userId)
                .orderByDesc(UserAddress::getId));
        if (!list.isEmpty()) {
            UserAddress target = new UserAddress();
            target.setId(list.get(0).getId());
            target.setIsDefault(1);
            userAddressMapper.updateById(target);
        }
    }

    private AddressResponse toResponse(UserAddress address) {
        return AddressResponse.builder()
                .id(address.getId())
                .receiver(address.getReceiver())
                .phone(address.getPhone())
                .province(address.getProvince())
                .city(address.getCity())
                .district(address.getDistrict())
                .detail(address.getDetail())
                .isDefault(address.getIsDefault())
                .build();
    }
}
