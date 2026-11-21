package com.dimbox.product.service;

import com.dimbox.common.result.PageResult;
import com.dimbox.product.dto.ProductDetailResponseDto;
import com.dimbox.product.dto.ProductListItemDto;
import com.dimbox.product.dto.ProductListQueryDto;
import com.dimbox.product.dto.ProductSaveRequestDto;
import com.dimbox.product.dto.SkuDetailResponseDto;
import com.dimbox.product.dto.SkuRequestDto;

/**
 * 商品服务接口。
 * <p>提供用户端商品浏览（分页列表、详情），以及管理端商品维护（新增、修改、上下架、删除）能力。</p>
 */
public interface ProductService {

    /**
     * 分页查询上架商品列表。
     *
     * @param query 查询条件（分类/品牌/关键字 + 分页）
     * @return 商品分页结果，price 为商品 SKU 最低价（分）
     */
    PageResult<ProductListItemDto> page(ProductListQueryDto query);

    /**
     * 分页查询全部商品（管理端），含下架商品。
     *
     * @param query 查询条件（分类/品牌/关键字 + 分页）
     * @return 商品分页结果
     */
    PageResult<ProductListItemDto> adminPage(ProductListQueryDto query);

    /**
     * 查询上架商品详情（含 SKU 列表）。
     *
     * @param id 商品 id
     * @return 商品详情
     */
    ProductDetailResponseDto detail(Long id);

    /**
     * 查询商品详情（管理端），含下架商品与全部款式。
     *
     * @param id 商品 id
     * @return 商品详情
     */
    ProductDetailResponseDto adminDetail(Long id);

    /**
     * 查询单个 SKU 详情（内部调用）。
     *
     * @param skuId SKU id
     * @return SKU 详情
     */
    SkuDetailResponseDto getSkuDetail(Long skuId);

    /**
     * 新增商品（含 SKU），默认状态为下架。
     *
     * @param request 商品及 SKU 信息
     * @return 新增商品的 id
     */
    Long create(ProductSaveRequestDto request);

    /**
     * 修改商品及 SKU。
     *
     * @param id      商品 id
     * @param request 商品及 SKU 信息
     */
    void update(Long id, ProductSaveRequestDto request);

    /**
     * 修改商品上下架状态。
     *
     * @param id     商品 id
     * @param status 状态（1 上架 / 0 下架）
     */
    void updateStatus(Long id, Integer status);

    /**
     * 为商品新增一个 SKU。
     */
    Long addSku(Long productId, SkuRequestDto request);

    /**
     * 修改单个 SKU。
     */
    void updateSku(Long skuId, SkuRequestDto request);

    /**
     * 修改单个 SKU 上下架状态。
     */
    void updateSkuStatus(Long skuId, Integer status);

    /**
     * 删除单个 SKU（逻辑删除），商品至少保留一个 SKU。
     */
    void deleteSku(Long skuId);

    /**
     * 删除商品及其 SKU（逻辑删除）。
     *
     * @param id 商品 id
     */
    void delete(Long id);
}
