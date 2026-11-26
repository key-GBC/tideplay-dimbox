package com.dimbox.promotion.dto.blindbox;

import com.dimbox.common.result.PageQuery;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeriesListQueryDto extends PageQuery {

    private Integer status;
}
