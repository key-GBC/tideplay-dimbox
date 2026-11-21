package com.dimbox.product.service;

import com.dimbox.product.dto.BrandRequestDto;
import com.dimbox.product.dto.BrandResponseDto;

import java.util.List;

/**
 * 品牌服务接口。
 * <p>提供品牌列表查询，以及品牌的新增、修改、删除能力。</p>
 */
public interface BrandService {

    /**
     * 查询启用的品牌列表。
     *
     * @return 品牌列表
     */
    List<BrandResponseDto> list();

    /**
     * 新增品牌。
     *
     * @param request 品牌信息
     * @return 新增品牌的 id
     */
    Long add(BrandRequestDto request);

    /**
     * 修改品牌。
     *
     * @param id      品牌 id
     * @param request 品牌信息
     */
    void update(Long id, BrandRequestDto request);

    /**
     * 删除品牌（逻辑删除）。
     *
     * @param id 品牌 id
     */
    void delete(Long id);
}
