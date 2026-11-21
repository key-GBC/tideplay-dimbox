package com.dimbox.promotion.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_seckill_activity")
public class SeckillActivity {

  @TableId(type = IdType.ASSIGN_ID)
  private Long id;

  private Long skuId;

  private Integer seckillPrice;

  private Integer stock;

  private LocalDateTime startTime;

  private LocalDateTime endTime;

  private Integer status;

  @TableLogic
  private Integer deleted;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

}
