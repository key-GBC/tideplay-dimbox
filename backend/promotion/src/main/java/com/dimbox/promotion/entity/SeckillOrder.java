package com.dimbox.promotion.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_seckill_order")
public class SeckillOrder {

  @TableId(type = IdType.ASSIGN_ID)
  private Long id;

  private Long activityId;

  private Long userId;

  private Long skuId;

  private String orderNo;

  private Integer status;

  @TableLogic
  private Integer deleted;

  private LocalDateTime createdAt;

}
