package com.dimbox.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_order")
public class Order {

  @TableId(type = IdType.ASSIGN_ID)
  private Long id;

  private String orderNo;
  private String requestId;
  private Long userId;
  private Integer status;
  private Integer totalAmount;
  private String receiver;
  private String phone;
  private String province;
  private String city;
  private String district;
  private String detail;
  private String remark;

  @TableLogic
  private Integer deleted;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;


}
