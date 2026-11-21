package com.dimbox.user.dto;

import com.dimbox.common.result.PageQuery;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserListQueryRequest extends PageQuery {

    private Integer role;

    private String username;

    private Integer status;

}
