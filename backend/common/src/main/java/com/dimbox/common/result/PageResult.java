package com.dimbox.common.result;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 分页返回结构。
 */
@Getter
@Setter
public class PageResult<T> {

    private long total;
    private List<T> records;
    private long pageNum;
    private long pageSize;

    public static <T> PageResult<T> of(long total, List<T> records, long pageNum, long pageSize) {
        PageResult<T> page = new PageResult<>();
        page.setTotal(total);
        page.setRecords(records);
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);
        return page;
    }
}
