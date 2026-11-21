package com.dimbox.common.result;

import lombok.Getter;
import lombok.Setter;

/**
 * 分页请求参数基类。前端传 pageNum/pageSize，服务端用 getOffset 计算偏移。
 */
@Getter
@Setter
public class PageQuery {

    private long pageNum = 1;
    private long pageSize = 10;

    public long getOffset() {
        return (pageNum - 1) * pageSize;
    }
}
