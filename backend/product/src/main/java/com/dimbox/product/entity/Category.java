package com.dimbox.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品分类实体，映射表 t_category。
 */
@Data
@TableName("t_category")
public class Category {

    /** 主键（雪花 ID） */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 父分类 id，0 表示顶级分类 */
    private Long parentId;

    /** 分类名 */
    private String name;

    /** 分类图标地址 */
    private String icon;

    /** 排序值，越小越靠前 */
    private Integer sort;

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
