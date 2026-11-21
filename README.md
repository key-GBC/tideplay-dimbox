# 次元匣社 · TidePlay / DIMBOX

潮玩商城全栈项目：基于 **Spring Cloud Alibaba 微服务** 的后端 + **Vue3** 前后端分离前端。覆盖「浏览 → 下单 → 支付 → 秒杀」完整交易闭环，并落地了防超卖、分布式一致性、管理员角色鉴权等工程实践。

## 技术亮点

- 微服务架构：Spring Cloud Gateway 统一网关、Nacos 注册发现、OpenFeign 服务调用、负载均衡
- 秒杀高并发：Redis 预扣库存 + Lua 原子扣减 + RabbitMQ 削峰异步落库 + Sentinel 限流 + 幂等 + 死信队列
- 防超卖：Redis 原子扣减 + MySQL 条件更新双层保障，压测验证不超卖
- 分布式一致性：跨服务「先扣库存后建单、失败回补」+ MQ 最终一致
- 鉴权体系：JWT 无状态认证 + 网关统一鉴权 + 管理员角色校验（`adminPaths`）
- 工程规范：统一响应/错误码/异常、全局文档（需求/设计/接口）、单元测试与压测

## 技术栈

| 层 | 技术 |
|---|---|
| 后端 | JDK 17、Spring Boot 3.2.4、Spring Cloud 2023.0.1、Spring Cloud Alibaba 2023.0.1.0 |
| 持久层 | MyBatis-Plus 3.5.7、MySQL 8 |
| 中间件 | Redis、RabbitMQ、Nacos、Sentinel |
| 前端 | Vue 3、Vite、Element Plus、Pinia、Vue Router、Axios |

## 服务架构

| 服务 | 应用名 | 端口 | 职责 |
|---|---|---|---|
| gateway | dimbox-gateway | 8080 | 统一入口、路由、鉴权、角色校验 |
| user | dimbox-user | 8081 | 注册登录、JWT、资料、地址 |
| product | dimbox-product | 8082 | 分类、品牌、商品、SKU、库存 |
| order | dimbox-order | 8083 | 下单、订单、幂等、库存扣减 |
| promotion | dimbox-promotion | 8084 | 秒杀活动、Redis 预扣、MQ 削峰 |
| payment | dimbox-payment | 8085 | 支付单、mock 支付、回调 |
| common | - | - | 统一响应、异常、分页等公共模块 |

## 项目结构

```text
backend/                  # Maven 多模块微服务
├── common/               # 公共模块
├── gateway/              # 网关
├── user/                 # 用户服务
├── product/              # 商品服务
├── order/                # 订单服务
├── promotion/            # 营销/秒杀服务
└── payment/              # 支付服务
frontend/
├── web/                  # 用户端商城
└── admin/                # 管理后台
docs/                     # 全局规范、接口总览、模块文档、复盘
```

## 核心功能

- 用户端：注册登录、商品浏览、下单、支付、订单管理、个人中心、秒杀抢购
- 管理端：商品/分类/品牌管理、SKU 级管理、秒杀活动管理、订单管理、用户管理

## 快速开始

### 1. 环境准备

- JDK 17、Maven、Node.js 18+
- 中间件：MySQL 8、Redis、Nacos、RabbitMQ（可用 Docker 部署）

### 2. 初始化数据库

按各服务的 `src/main/resources/sql/` 目录执行建表脚本，并在 `t_user` 中指定一个管理员：

```sql
UPDATE t_user SET role = 1 WHERE username = '你的管理员账号';
```

### 3. 配置本地环境变量

每个服务的 `application.yml` 使用 `${VAR}` 占位符，真实值放在 `application-local.yml`（已 gitignore）。启动前为每个服务复制一份并填入真实连接信息，变量清单见各服务的 `application-local.yml` 样板。

### 4. 启动后端

依次启动：`gateway` → `user` → `product` → `order` → `payment` → `promotion`（后三者无强顺序）。

### 5. 启动前端

```bash
cd frontend/web && npm install && npm run dev    # 用户端 http://localhost:5173
cd frontend/admin && npm install && npm run dev  # 管理端
```

## 文档

- `docs/02-全局规范.md`：统一响应、错误码、鉴权、数据约定
- `docs/03-接口总览.md`：全部接口统一索引
- `docs/services/*`：各模块的需求 / 设计 / 接口文档
- `docs/05-项目复盘与面试准备.md`：核心设计复盘与面试要点

## 说明

本项目为学习与求职展示用途，凭据已外置到 `application-local.yml`，仓库不含任何真实密码。
