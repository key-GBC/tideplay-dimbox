package com.dimbox.promotion.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_blindbox_item")
public class BlindBoxItem {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long seriesId;

    private Long skuId;

    private String styleName;

    private String image;

    private Integer probability;

    private Integer isHidden;

    @TableLogic
    private Integer deleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
