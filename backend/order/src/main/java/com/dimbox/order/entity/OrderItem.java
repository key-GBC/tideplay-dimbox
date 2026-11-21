package com.dimbox.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_order_item")
public class OrderItem {

  @TableId(type = IdType.ASSIGN_ID)
  private Long id;

  private Long orderId;
  private Long productId;
  private Long skuId;
  private String skuName;
  private String image;
  private Integer price;
  private Integer quantity;

  @TableLogic
  private Integer deleted;

  private LocalDateTime createdAt;


}
