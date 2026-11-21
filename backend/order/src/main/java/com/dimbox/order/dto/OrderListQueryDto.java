package com.dimbox.order.dto;

import com.dimbox.common.result.PageQuery;
import lombok.Getter;
import lombok.Setter;

/**
 * 订单分页查询参数，继承公共分页参数并新增状态过滤。
 */
@Getter
@Setter
public class OrderListQueryDto extends PageQuery {

    /** 订单状态，为空表示不过滤 */
    private Integer status;

    private String orderNo;
}
