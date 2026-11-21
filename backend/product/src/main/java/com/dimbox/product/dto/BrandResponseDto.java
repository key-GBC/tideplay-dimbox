package com.dimbox.product.dto;

import lombok.Data;

/**
 * 品牌信息返回对象（后端 → 前端，用户端展示 / 管理端管理）。
 */
@Data
public class BrandResponseDto {

    /** 品牌 id */
    private Long id;

    /** 品牌名 */
    private String name;

    /** 品牌 Logo 地址 */
    private String logo;

    /** 品牌描述 */
    private String description;

    /** 状态：1 启用 / 0 禁用 */
    private Integer status;
}
