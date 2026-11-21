package com.dimbox.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dimbox.common.result.PageResult;
import com.dimbox.product.dto.ProductDetailResponseDto;
import com.dimbox.product.dto.ProductListItemDto;
import com.dimbox.product.dto.ProductListQueryDto;
import com.dimbox.product.dto.ProductSaveRequestDto;
import com.dimbox.product.dto.SkuRequestDto;
import com.dimbox.product.dto.SkuDetailResponseDto;
import com.dimbox.product.dto.SkuResponseDto;
import com.dimbox.product.entity.Product;
import com.dimbox.product.entity.ProductSku;
import com.dimbox.product.enums.ProductErrorCode;
import com.dimbox.product.mapper.BrandMapper;
import com.dimbox.product.mapper.CategoryMapper;
import com.dimbox.product.mapper.ProductMapper;
import com.dimbox.product.mapper.ProductSkuMapper;
import com.dimbox.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品服务实现。
 * <p>负责商品（SPU）与 SKU 的查询与维护，SKU 与商品操作在同一事务内保证一致性。</p>
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final ProductSkuMapper productSkuMapper;
    private final CategoryMapper categoryMapper;
    private final BrandMapper brandMapper;

    @Override
    public PageResult<ProductListItemDto> page(ProductListQueryDto query) {
        return doPage(query, true);
    }

    @Override
    public PageResult<ProductListItemDto> adminPage(ProductListQueryDto query) {
        return doPage(query, false);
    }

    /**
     * 分页查询商品；onlyOnSale 为 true 时只查上架商品，否则查全部（管理端）。
     */
    private PageResult<ProductListItemDto> doPage(ProductListQueryDto query, boolean onlyOnSale) {
        Page<Product> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(onlyOnSale, Product::getStatus, 1)
                .eq(query.getCategoryId() != null, Product::getCategoryId, query.getCategoryId())
                .eq(query.getBrandId() != null, Product::getBrandId, query.getBrandId())
                .like(query.getKeyword() != null && !query.getKeyword().isBlank(), Product::getName, query.getKeyword())
                .orderByDesc(Product::getId);
        Page<Product> result = productMapper.selectPage(page, wrapper);

        // 批量查本页商品的 SKU，在内存里算出每个商品的最低价（避免逐商品查库）
        List<Long> ids = result.getRecords().stream().map(Product::getId).collect(Collectors.toList());
        Map<Long, Integer> minPrice = new HashMap<>();
        if (!ids.isEmpty()) {
            List<ProductSku> skus = productSkuMapper.selectList(
                    new LambdaQueryWrapper<ProductSku>().in(ProductSku::getProductId, ids));
            for (ProductSku sku : skus) {
                minPrice.merge(sku.getProductId(), sku.getPrice(), Math::min);
            }
        }

        List<ProductListItemDto> items = result.getRecords().stream().map(p -> {
            ProductListItemDto item = new ProductListItemDto();
            item.setId(p.getId());
            item.setName(p.getName());
            item.setSubTitle(p.getSubTitle());
            item.setMainImage(p.getMainImage());
            item.setPrice(minPrice.getOrDefault(p.getId(), 0));
            item.setStatus(p.getStatus());
            return item;
        }).collect(Collectors.toList());

        return PageResult.of(result.getTotal(), items, query.getPageNum(), query.getPageSize());
    }

    @Override
    public ProductDetailResponseDto detail(Long id) {
        return detailInternal(id, true);
    }

    @Override
    public ProductDetailResponseDto adminDetail(Long id) {
        return detailInternal(id, false);
    }

    /**
     * 查询商品详情；onlyOnSale 为 true 时仅对「上架」商品开放且只返回上架款式。
     */
    private ProductDetailResponseDto detailInternal(Long id, boolean onlyOnSale) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw ProductErrorCode.PRODUCT_NOT_FOUND.exception();
        }
        if (onlyOnSale && (product.getStatus() == null || product.getStatus() != 1)) {
            throw ProductErrorCode.PRODUCT_NOT_FOUND.exception();
        }
        List<SkuResponseDto> skus = productSkuMapper.selectList(
                new LambdaQueryWrapper<ProductSku>()
                        .eq(ProductSku::getProductId, id)
                        .eq(onlyOnSale, ProductSku::getStatus, 1))
                .stream().map(this::toSkuResponse).collect(Collectors.toList());

        ProductDetailResponseDto dto = new ProductDetailResponseDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSubTitle(product.getSubTitle());
        dto.setMainImage(product.getMainImage());
        dto.setDetail(product.getDetail());
        dto.setCategoryId(product.getCategoryId());
        dto.setBrandId(product.getBrandId());
        dto.setSkus(skus);
        return dto;
    }

    /**
     * 查询单个 SKU 详情，供订单服务下单快照使用。
     */
    @Override
    public SkuDetailResponseDto getSkuDetail(Long skuId) {
        ProductSku sku = productSkuMapper.selectById(skuId);
        if (sku == null) {
            throw ProductErrorCode.SKU_NOT_FOUND.exception();
        }
        SkuDetailResponseDto dto = new SkuDetailResponseDto();
        dto.setId(sku.getId());
        dto.setProductId(sku.getProductId());
        dto.setSkuName(sku.getSkuName());
        dto.setImage(sku.getImage());
        dto.setPrice(sku.getPrice());
        dto.setStock(sku.getStock());
        dto.setStatus(sku.getStatus());
        return dto;
    }

    @Override
    @Transactional
    public Long create(ProductSaveRequestDto request) {
        // 先校验分类/品牌存在，再写库
        checkCategoryAndBrand(request);
        Product product = new Product();
        product.setCategoryId(request.getCategoryId());
        product.setBrandId(request.getBrandId());
        product.setName(request.getName());
        product.setSubTitle(request.getSubTitle());
        product.setMainImage(request.getMainImage());
        product.setDetail(request.getDetail());
        // 新建商品默认下架，由运营通过上下架接口发布
        product.setStatus(0);
        productMapper.insert(product);
        saveSkus(product.getId(), request.getSkus());
        return product.getId();
    }

    @Override
    @Transactional
    public void update(Long id, ProductSaveRequestDto request) {
        if (productMapper.selectById(id) == null) {
            throw ProductErrorCode.PRODUCT_NOT_FOUND.exception();
        }
        checkCategoryAndBrand(request);

        // 更新 SPU 字段；用 LambdaUpdateWrapper 显式 set，允许把可空字段清空
        LambdaUpdateWrapper<Product> wrapper = new LambdaUpdateWrapper<Product>()
                .eq(Product::getId, id)
                .set(Product::getCategoryId, request.getCategoryId())
                .set(Product::getBrandId, request.getBrandId())
                .set(Product::getName, request.getName())
                .set(Product::getSubTitle, request.getSubTitle())
                .set(Product::getMainImage, request.getMainImage())
                .set(Product::getDetail, request.getDetail());
        productMapper.update(null, wrapper);

        // P0 简化：删除旧 SKU 后重建（会重置 SKU id 和库存，订单模块接入后需改成按 id 更新）
        productSkuMapper.delete(new LambdaQueryWrapper<ProductSku>().eq(ProductSku::getProductId, id));
        saveSkus(id, request.getSkus());
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        if (productMapper.selectById(id) == null) {
            throw ProductErrorCode.PRODUCT_NOT_FOUND.exception();
        }
        Product product = new Product();
        product.setId(id);
        product.setStatus(status);
        productMapper.updateById(product);
    }

    @Override
    @Transactional
    public Long addSku(Long productId, SkuRequestDto request) {
        if (productMapper.selectById(productId) == null) {
            throw ProductErrorCode.PRODUCT_NOT_FOUND.exception();
        }
        ProductSku sku = new ProductSku();
        sku.setProductId(productId);
        sku.setSkuName(request.getSkuName());
        sku.setImage(request.getImage());
        sku.setPrice(request.getPrice());
        sku.setStock(request.getStock());
        sku.setStatus(1);
        productSkuMapper.insert(sku);
        return sku.getId();
    }

    @Override
    public void updateSku(Long skuId, SkuRequestDto request) {
        getSku(skuId);
        ProductSku sku = new ProductSku();
        sku.setId(skuId);
        sku.setSkuName(request.getSkuName());
        sku.setImage(request.getImage());
        sku.setPrice(request.getPrice());
        sku.setStock(request.getStock());
        productSkuMapper.updateById(sku);
    }

    @Override
    public void updateSkuStatus(Long skuId, Integer status) {
        getSku(skuId);
        ProductSku sku = new ProductSku();
        sku.setId(skuId);
        sku.setStatus(status);
        productSkuMapper.updateById(sku);
    }

    @Override
    @Transactional
    public void deleteSku(Long skuId) {
        ProductSku sku = getSku(skuId);
        Long count = productSkuMapper.selectCount(
                new LambdaQueryWrapper<ProductSku>().eq(ProductSku::getProductId, sku.getProductId()));
        if (count != null && count <= 1) {
            throw ProductErrorCode.SKU_LAST_ONE.exception();
        }
        productSkuMapper.deleteById(skuId);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (productMapper.selectById(id) == null) {
            throw ProductErrorCode.PRODUCT_NOT_FOUND.exception();
        }
        // 商品与 SKU 一起逻辑删除
        productMapper.deleteById(id);
        productSkuMapper.delete(new LambdaQueryWrapper<ProductSku>().eq(ProductSku::getProductId, id));
    }

    /**
     * 校验商品关联的分类与品牌是否存在。
     */
    private void checkCategoryAndBrand(ProductSaveRequestDto request) {
        if (categoryMapper.selectById(request.getCategoryId()) == null) {
            throw ProductErrorCode.CATEGORY_NOT_FOUND.exception();
        }
        if (request.getBrandId() != null && brandMapper.selectById(request.getBrandId()) == null) {
            throw ProductErrorCode.BRAND_NOT_FOUND.exception();
        }
    }

    /**
     * 批量插入 SKU。
     */
    private void saveSkus(Long productId, List<SkuRequestDto> skus) {
        for (SkuRequestDto req : skus) {
            ProductSku sku = new ProductSku();
            sku.setProductId(productId);
            sku.setSkuName(req.getSkuName());
            sku.setImage(req.getImage());
            sku.setPrice(req.getPrice());
            sku.setStock(req.getStock());
            sku.setStatus(1);
            productSkuMapper.insert(sku);
        }
    }

    /**
     * SKU 实体转返回 DTO。
     */
    private SkuResponseDto toSkuResponse(ProductSku sku) {
        SkuResponseDto dto = new SkuResponseDto();
        dto.setId(sku.getId());
        dto.setSkuName(sku.getSkuName());
        dto.setImage(sku.getImage());
        dto.setPrice(sku.getPrice());
        dto.setStock(sku.getStock());
        dto.setStatus(sku.getStatus());
        return dto;
    }

    private ProductSku getSku(Long skuId) {
        ProductSku sku = productSkuMapper.selectById(skuId);
        if (sku == null) {
            throw ProductErrorCode.SKU_NOT_FOUND.exception();
        }
        return sku;
    }


}
