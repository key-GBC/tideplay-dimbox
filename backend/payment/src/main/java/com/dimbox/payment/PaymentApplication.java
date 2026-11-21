package com.dimbox.payment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * payment 支付服务启动类。
 * <p>{@code @MapperScan} 扫描本服务 Mapper 接口；{@code @EnableFeignClients} 开启对 order 服务的声明式调用。</p>
 */
@SpringBootApplication(scanBasePackages = "com.dimbox")
@MapperScan("com.dimbox.payment.mapper")
@EnableFeignClients(basePackages = "com.dimbox.payment.client")
public class PaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }
}

