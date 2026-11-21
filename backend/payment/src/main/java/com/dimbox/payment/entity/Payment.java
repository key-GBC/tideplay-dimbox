package com.dimbox.payment.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 支付单实体，映射表 t_payment。
 * <p>一笔待支付订单对应一张支付单，order_no 唯一，用于创建幂等。</p>
 */
@Data
@TableName("t_payment")
public class Payment {

  /** 主键（雪花 ID） */
  @TableId(type = IdType.ASSIGN_ID)
  private Long id;

  /** 支付单号 */
  private String paymentNo;

  /** 关联的订单号，唯一，用于创建幂等 */
  private String orderNo;

  /** 下单用户 id */
  private Long userId;

  /** 支付金额，单位：分 */
  private Integer amount;

  /** 支付渠道，P0 固定为 mock */
  private String channel;

  /** 状态：10 待支付 / 20 已支付 */
  private Integer status;

  /** 支付成功时间 */
  private LocalDateTime paidAt;

  /** 逻辑删除标记：0 未删 / 1 已删 */
  @TableLogic
  private Integer deleted;

  /** 创建时间 */
  private LocalDateTime createdAt;

  /** 更新时间 */
  private LocalDateTime updatedAt;

}
