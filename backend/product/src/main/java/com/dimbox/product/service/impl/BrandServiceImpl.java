package com.dimbox.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dimbox.product.dto.BrandRequestDto;
import com.dimbox.product.dto.BrandResponseDto;
import com.dimbox.product.entity.Brand;
import com.dimbox.product.enums.ProductErrorCode;
import com.dimbox.product.mapper.BrandMapper;
import com.dimbox.product.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 品牌服务实现。
 */
@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandMapper brandMapper;

    @Override
    public List<BrandResponseDto> list() {
        // 只返回启用品牌，按创建时间倒序
        List<Brand> brands = brandMapper.selectList(
                new LambdaQueryWrapper<Brand>().eq(Brand::getStatus, 1).orderByDesc(Brand::getId));
        return brands.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public Long add(BrandRequestDto request) {
        Brand brand = new Brand();
        brand.setName(request.getName());
        brand.setLogo(request.getLogo());
        brand.setDescription(request.getDescription());
        // 未传状态时默认启用
        brand.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        brandMapper.insert(brand);
        return brand.getId();
    }

    @Override
    public void update(Long id, BrandRequestDto request) {
        if (brandMapper.selectById(id) == null) {
            throw ProductErrorCode.BRAND_NOT_FOUND.exception();
        }
        // 只更新非空字段（MyBatis-Plus 默认忽略 null 字段）
        Brand brand = new Brand();
        brand.setId(id);
        brand.setName(request.getName());
        brand.setLogo(request.getLogo());
        brand.setDescription(request.getDescription());
        brand.setStatus(request.getStatus());
        brandMapper.updateById(brand);
    }

    @Override
    public void delete(Long id) {
        if (brandMapper.selectById(id) == null) {
            throw ProductErrorCode.BRAND_NOT_FOUND.exception();
        }
        // 逻辑删除
        brandMapper.deleteById(id);
    }

    /**
     * 实体转返回 DTO，避免把内部字段（deleted、时间戳等）暴露给前端。
     */
    private BrandResponseDto toResponse(Brand brand) {
        BrandResponseDto dto = new BrandResponseDto();
        dto.setId(brand.getId());
        dto.setName(brand.getName());
        dto.setLogo(brand.getLogo());
        dto.setDescription(brand.getDescription());
        dto.setStatus(brand.getStatus());
        return dto;
    }
}
