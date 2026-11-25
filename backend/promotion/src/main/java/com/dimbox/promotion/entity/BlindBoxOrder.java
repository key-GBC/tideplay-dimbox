package com.dimbox.promotion.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_blindbox_order")
public class BlindBoxOrder {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String orderNo;

    private Long userId;

    private Long seriesId;

    private Long itemId;

    private Long skuId;

    private Integer price;

    private Integer isHidden;

    private LocalDateTime createdAt;
}
