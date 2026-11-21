package com.dimbox.gateway.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 网关自定义配置。
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "dimbox.gateway")
public class GatewayProperties {

    /**
     * 鉴权白名单，Ant 风格路径，匹配网关收到的原始请求路径。
     */
    private List<String> whitelist = new ArrayList<>();

    private List<String> adminPaths = new ArrayList<>();
}
