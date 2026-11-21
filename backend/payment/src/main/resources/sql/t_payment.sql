CREATE TABLE IF NOT EXISTS t_payment (
                                         id BIGINT NOT NULL COMMENT '主键(雪花ID)',
                                         payment_no VARCHAR(64) NOT NULL COMMENT '支付单号',
    order_no VARCHAR(64) NOT NULL COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    amount INT NOT NULL COMMENT '支付金额(分)',
    channel VARCHAR(32) NOT NULL DEFAULT 'mock' COMMENT '支付渠道',
    status TINYINT NOT NULL DEFAULT 10 COMMENT '10待支付 20已支付',
    paid_at DATETIME DEFAULT NULL COMMENT '支付时间',
    deleted TINYINT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_payment_no (payment_no),
    UNIQUE KEY uk_payment_order (order_no),
    KEY idx_payment_user (user_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付单表';