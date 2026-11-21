package com.dimbox.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 品牌实体，映射表 t_brand。
 */
@Data
@TableName("t_brand")
public class Brand {

    /** 主键（雪花 ID） */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 品牌名 */
    private String name;

    /** 品牌 Logo 地址 */
    private String logo;

    /** 品牌描述 */
    private String description;

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
