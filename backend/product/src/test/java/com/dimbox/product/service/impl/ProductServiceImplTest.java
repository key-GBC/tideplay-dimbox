package com.dimbox.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dimbox.common.result.PageResult;
import com.dimbox.product.dto.ProductListItemDto;
import com.dimbox.product.dto.ProductListQueryDto;
import com.dimbox.product.entity.Product;
import com.dimbox.product.entity.ProductSku;
import com.dimbox.product.mapper.BrandMapper;
import com.dimbox.product.mapper.CategoryMapper;
import com.dimbox.product.mapper.ProductMapper;
import com.dimbox.product.mapper.ProductSkuMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * ProductServiceImpl 单元测试。
 * 重点验证商品列表的「最低价」计算逻辑。
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductMapper productMapper;
    @Mock
    private ProductSkuMapper productSkuMapper;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private BrandMapper brandMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void page_返回SKU最低价() {
        // 查询参数：第 1 页，每页 10 条
        ProductListQueryDto query = new ProductListQueryDto();
        query.setPageNum(1);
        query.setPageSize(10);

        Product product = new Product();
        product.setId(100L);
        product.setName("MOLLY 系列盲盒");
        product.setStatus(1);

        // mock 分页查询结果
        Page<Product> pageResult = new Page<>();
        pageResult.setRecords(List.of(product));
        pageResult.setTotal(1);
        when(productMapper.selectPage(
                ArgumentMatchers.<Page<Product>>any(),
                ArgumentMatchers.<Wrapper<Product>>any()))
                .thenReturn(pageResult);

        // 两个 SKU：价格 6900 和 5900，预期最低价为 5900
        ProductSku sku1 = new ProductSku();
        sku1.setProductId(100L);
        sku1.setPrice(6900);
        ProductSku sku2 = new ProductSku();
        sku2.setProductId(100L);
        sku2.setPrice(5900);
        when(productSkuMapper.selectList(ArgumentMatchers.<Wrapper<ProductSku>>any()))
                .thenReturn(List.of(sku1, sku2));

        PageResult<ProductListItemDto> result = productService.page(query);

        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getRecords()).hasSize(1);
        assertThat(result.getRecords().get(0).getPrice()).isEqualTo(5900);
    }
}
