package com.dimbox.product.dto;

import lombok.Data;

/**
 * 商品列表项返回对象（后端 → 用户端）。
 */
@Data
public class ProductListItemDto {

    /** 商品 id */
    private Long id;

    /** 商品名 */
    private String name;

    /** 副标题 */
    private String subTitle;

    /** 主图地址 */
    private String mainImage;

    /** 商品最低价，单位：分 */
    private Integer price;

    /** 状态：1 上架 / 0 下架 */
    private Integer status;
}
