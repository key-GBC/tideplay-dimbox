package com.dimbox.user.service;

import com.dimbox.user.dto.AddressRequest;
import com.dimbox.user.dto.AddressResponse;

import java.util.List;

public interface AddressService {

    List<AddressResponse> list(Long userId);

    Long add(Long userId, AddressRequest request);

    void update(Long userId, Long addressId, AddressRequest request);

    void delete(Long userId, Long addressId);

    void setDefault(Long userId, Long addressId);
}
