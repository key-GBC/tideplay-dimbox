package com.dimbox.user.service;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.dimbox.common.exception.BizException;
import com.dimbox.user.dto.AddressRequest;
import com.dimbox.user.entity.UserAddress;
import com.dimbox.user.mapper.UserAddressMapper;
import com.dimbox.user.service.impl.AddressServiceImpl;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    @Mock
    private UserAddressMapper userAddressMapper;

    @InjectMocks
    private AddressServiceImpl addressService;

    @BeforeAll
    static void initTableInfo() {
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(new MybatisConfiguration(), "");
        TableInfoHelper.initTableInfo(assistant, UserAddress.class);
    }

    @Test
    void add_whenExceedLimit_throws() {
        when(userAddressMapper.selectCount(any())).thenReturn(20L);

        BizException ex = assertThrows(BizException.class, () -> addressService.add(1L, buildRequest()));

        assertEquals(400, ex.getCode());
    }

    @Test
    void add_whenDefault_clearsOldDefaultAndInserts() {
        when(userAddressMapper.selectCount(any())).thenReturn(0L);
        AddressRequest request = buildRequest();
        request.setIsDefault(1);

        addressService.add(1L, request);

        verify(userAddressMapper).update(any(), any());
        verify(userAddressMapper).insert(any(UserAddress.class));
    }

    @Test
    void update_whenNotFound_throws() {
        when(userAddressMapper.selectById(5L)).thenReturn(null);

        BizException ex = assertThrows(BizException.class, () -> addressService.update(1L, 5L, buildRequest()));

        assertEquals(404, ex.getCode());
    }

    @Test
    void update_whenNotOwner_throws() {
        when(userAddressMapper.selectById(5L)).thenReturn(buildAddress(5L, 99L));

        BizException ex = assertThrows(BizException.class, () -> addressService.update(1L, 5L, buildRequest()));

        assertEquals(403, ex.getCode());
    }

    @Test
    void update_whenOwner_updates() {
        when(userAddressMapper.selectById(5L)).thenReturn(buildAddress(5L, 1L));

        addressService.update(1L, 5L, buildRequest());

        verify(userAddressMapper).update(any(), any());
    }

    @Test
    void delete_whenDefault_promotesLatest() {
        UserAddress address = buildAddress(5L, 1L);
        address.setIsDefault(1);
        when(userAddressMapper.selectById(5L)).thenReturn(address);
        when(userAddressMapper.selectList(any())).thenReturn(List.of(buildAddress(9L, 1L)));

        addressService.delete(1L, 5L);

        verify(userAddressMapper).deleteById(5L);
        verify(userAddressMapper).updateById(any(UserAddress.class));
    }

    @Test
    void setDefault_clearsAndSets() {
        when(userAddressMapper.selectById(5L)).thenReturn(buildAddress(5L, 1L));

        addressService.setDefault(1L, 5L);

        verify(userAddressMapper).update(any(), any());
        verify(userAddressMapper).updateById(any(UserAddress.class));
    }

    private AddressRequest buildRequest() {
        AddressRequest request = new AddressRequest();
        request.setReceiver("张三");
        request.setPhone("13800138000");
        request.setProvince("广东省");
        request.setCity("深圳市");
        request.setDistrict("南山区");
        request.setDetail("科技园 1 号");
        request.setIsDefault(0);
        return request;
    }

    private UserAddress buildAddress(Long id, Long userId) {
        UserAddress address = new UserAddress();
        address.setId(id);
        address.setUserId(userId);
        address.setReceiver("张三");
        return address;
    }
}
