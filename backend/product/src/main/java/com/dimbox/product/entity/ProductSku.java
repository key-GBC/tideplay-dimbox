package com.dimbox.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品 SKU 实体，映射表 t_product_sku。
 * <p>一个商品（SPU）对应多个 SKU，SKU 承载具体的款式、价格与库存。</p>
 */
@Data
@TableName("t_product_sku")
public class ProductSku {

    /** 主键（雪花 ID） */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 所属商品 id */
    private Long productId;

    /** 款式/规格名 */
    private String skuName;

    /** SKU 图片地址 */
    private String image;

    /** 价格，单位：分 */
    private Integer price;

    /** 库存数量 */
    private Integer stock;

    /** 状态：1 启用 / 0 禁用 */
    private Integer status;

    /** 逻辑删除标记：0 未删 / 1 已删 */
    @TableLogic
    private Integer deleted;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
