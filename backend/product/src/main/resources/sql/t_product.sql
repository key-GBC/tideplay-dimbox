-- product 模块建表：分类、品牌、商品(SPU)、SKU
CREATE TABLE IF NOT EXISTS t_category (
                                          id BIGINT NOT NULL,
                                          parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父分类ID，0为顶级',
                                          name VARCHAR(64) NOT NULL COMMENT '分类名',
    icon VARCHAR(255) DEFAULT NULL COMMENT '图标',
    sort INT NOT NULL DEFAULT 0 COMMENT '排序',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态:1启用 0禁用',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_category_parent (parent_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

CREATE TABLE IF NOT EXISTS t_brand (
                                       id BIGINT NOT NULL,
                                       name VARCHAR(64) NOT NULL COMMENT '品牌名',
    logo VARCHAR(255) DEFAULT NULL COMMENT 'Logo',
    description VARCHAR(500) DEFAULT NULL COMMENT '描述',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态:1启用 0禁用',
    deleted TINYINT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='品牌表';

CREATE TABLE IF NOT EXISTS t_product (
                                         id BIGINT NOT NULL,
                                         category_id BIGINT NOT NULL COMMENT '分类ID',
                                         brand_id BIGINT DEFAULT NULL COMMENT '品牌ID',
                                         name VARCHAR(128) NOT NULL COMMENT '商品名',
    sub_title VARCHAR(255) DEFAULT NULL COMMENT '副标题',
    main_image VARCHAR(255) DEFAULT NULL COMMENT '主图',
    detail TEXT COMMENT '详情',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态:1上架 0下架',
    deleted TINYINT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_product_category (category_id),
    KEY idx_product_brand (brand_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品SPU表';

CREATE TABLE IF NOT EXISTS t_product_sku (
                                             id BIGINT NOT NULL,
                                             product_id BIGINT NOT NULL COMMENT '商品ID',
                                             sku_name VARCHAR(128) NOT NULL COMMENT '规格/款式名',
    image VARCHAR(255) DEFAULT NULL COMMENT 'SKU图片',
    price INT NOT NULL COMMENT '价格(分)',
    stock INT NOT NULL DEFAULT 0 COMMENT '库存',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态:1启用 0禁用',
    deleted TINYINT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_sku_product (product_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品SKU表';