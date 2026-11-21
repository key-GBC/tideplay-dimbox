package com.dimbox.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品 SPU 实体，映射表 t_product。
 * <p>SPU 描述商品主体，具体规格/款式/价格/库存见 {@link ProductSku}。</p>
 */
@Data
@TableName("t_product")
public class Product {

    /** 主键（雪花 ID） */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 所属分类 id */
    private Long categoryId;

    /** 所属品牌 id，可为空 */
    private Long brandId;

    /** 商品名 */
    private String name;

    /** 副标题 */
    private String subTitle;

    /** 主图地址 */
    private String mainImage;

    /** 商品详情 */
    private String detail;

    /** 状态：1 上架 / 0 下架 */
    private Integer status;

    /** 逻辑删除标记：0 未删 / 1 已删 */
    @TableLogic
    private Integer deleted;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
