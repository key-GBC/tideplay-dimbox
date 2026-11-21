-- order 模块建表：订单 + 订单明细
CREATE TABLE IF NOT EXISTS t_order (
    id BIGINT NOT NULL COMMENT '主键(雪花ID)',
    order_no VARCHAR(64) NOT NULL COMMENT '订单号',
    request_id VARCHAR(64) NOT NULL COMMENT '客户端幂等键',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    status TINYINT NOT NULL DEFAULT 10 COMMENT '10待支付 50已取消',
    total_amount INT NOT NULL COMMENT '订单总额(分)',
    receiver VARCHAR(32) NOT NULL COMMENT '收货人快照',
    phone VARCHAR(20) NOT NULL COMMENT '手机号快照',
    province VARCHAR(64) NOT NULL COMMENT '省快照',
    city VARCHAR(64) NOT NULL COMMENT '市快照',
    district VARCHAR(64) NOT NULL COMMENT '区快照',
    detail VARCHAR(255) NOT NULL COMMENT '详细地址快照',
    remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
    deleted TINYINT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    UNIQUE KEY uk_order_request (request_id),
    KEY idx_order_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

CREATE TABLE IF NOT EXISTS t_order_item (
    id BIGINT NOT NULL,
    order_id BIGINT NOT NULL COMMENT '订单ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    sku_name VARCHAR(128) NOT NULL COMMENT 'SKU名快照',
    image VARCHAR(255) DEFAULT NULL COMMENT '图片快照',
    price INT NOT NULL COMMENT '成交单价(分)',
    quantity INT NOT NULL COMMENT '数量',
    deleted TINYINT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_order_item_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';
