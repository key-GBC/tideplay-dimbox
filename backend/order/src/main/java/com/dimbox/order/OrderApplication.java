package com.dimbox.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 订单服务启动类。
 * <p>开启组件扫描、Mapper 扫描与 Feign 客户端扫描。</p>
 */
@SpringBootApplication(scanBasePackages = "com.dimbox")
@MapperScan("com.dimbox.order.mapper")
@EnableFeignClients(basePackages = "com.dimbox.order.client")
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

}
