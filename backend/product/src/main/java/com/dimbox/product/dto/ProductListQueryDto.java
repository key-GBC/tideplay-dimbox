package com.dimbox.product.dto;

import com.dimbox.common.result.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品列表查询参数（用户端 → 后端，筛选 + 分页）。
 * <p>继承 {@link PageQuery}，自带 pageNum / pageSize 分页字段。</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductListQueryDto extends PageQuery {

    /** 按分类筛选，可为空 */
    private Long categoryId;

    /** 按品牌筛选，可为空 */
    private Long brandId;

    /** 按商品名关键字模糊搜索，可为空 */
    private String keyword;
}
