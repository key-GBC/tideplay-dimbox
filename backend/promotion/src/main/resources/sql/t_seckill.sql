CREATE TABLE IF NOT EXISTS t_seckill_activity (
    id BIGINT NOT NULL COMMENT '主键(雪花ID)',
    sku_id BIGINT NOT NULL COMMENT '秒杀SKU',
    seckill_price INT NOT NULL COMMENT '秒杀价(分)',
    stock INT NOT NULL COMMENT '秒杀库存',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '1启用 0禁用',
    deleted TINYINT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_seckill_time (start_time, end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀活动表';

CREATE TABLE IF NOT EXISTS t_seckill_order (
    id BIGINT NOT NULL,
    activity_id BIGINT NOT NULL COMMENT '活动ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    order_no VARCHAR(64) NOT NULL COMMENT '订单号',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0待扣库存 1已扣库存',
    deleted TINYINT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_seckill_user (activity_id, user_id),
    KEY idx_seckill_order_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀订单记录表';
