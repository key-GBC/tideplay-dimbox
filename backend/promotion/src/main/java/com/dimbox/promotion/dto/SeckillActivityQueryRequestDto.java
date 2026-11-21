package com.dimbox.promotion.dto;

import com.dimbox.common.result.PageQuery;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeckillActivityQueryRequestDto extends PageQuery {
    private Integer status;
}
