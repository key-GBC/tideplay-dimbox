package com.dimbox.promotion;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.dimbox")
@MapperScan("com.dimbox.promotion.mapper")
@EnableFeignClients(basePackages = "com.dimbox.promotion.client")
public class PromotionApplication {
    public static void main(String[] args) {
        SpringApplication.run(PromotionApplication.class, args);
    }
}
