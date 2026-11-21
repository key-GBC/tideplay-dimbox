package com.dimbox.order.dto;

import lombok.Data;

@Data
public class OrderAdminListDto extends OrderListItemDto {

    private Long userId;
}
