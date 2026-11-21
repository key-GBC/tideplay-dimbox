package com.dimbox.gateway.config;

import com.dimbox.gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 全局鉴权过滤器。
 * <p>
 * 1. 白名单内的请求直接放行（白名单格式为「方法:路径」，如 GET:/api/product/list）。
 * 2. 其余请求校验 JWT，解析出用户 id 后通过 X-User-Id 请求头透传给下游服务。
 * </p>
 */
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    /** JWT 鉴权头前缀。 */
    private static final String BEARER_PREFIX = "Bearer ";
    /** 透传给下游服务的用户身份请求头。 */
    private static final String USER_ID_HEADER = "X-User-Id";

    private static final String USER_ROLE_HEADER = "X-User-Role";

    private static final int ADMIN_ROLE = 1;

    private final JwtUtil jwtUtil;
    private final GatewayProperties properties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public AuthGlobalFilter(JwtUtil jwtUtil, GatewayProperties properties) {
        this.jwtUtil = jwtUtil;
        this.properties = properties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (isWhitelisted(exchange)) {
            return chain.filter(exchange);
        }

        String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            return unauthorized(exchange);
        }

        String token = authorization.substring(BEARER_PREFIX.length());
        try {
            Claims claims = jwtUtil.parseClaims(token);
            String userId = claims.getSubject();
            Integer role = claims.get("role", Integer.class);

            ServerHttpRequest mutated = exchange.getRequest().mutate()
                    .headers(headers -> {
                        headers.set(USER_ID_HEADER, userId);
                        headers.set(USER_ROLE_HEADER, String.valueOf(role == null ? 0 : role));
                    })
                    .build();

            if (isAdminPath(exchange) && (role == null || role != ADMIN_ROLE)) {
                return forbidden(exchange);
            }

            return chain.filter(exchange.mutate().request(mutated).build());
        } catch (Exception e) {
            return unauthorized(exchange);
        }
    }

    /**
     * 判断请求是否命中白名单。
     * 白名单条目格式为「方法:路径」，方法写 * 表示不限方法。
     */
    private boolean isWhitelisted(ServerWebExchange exchange) {
        String method = exchange.getRequest().getMethod().name();
        String path = exchange.getRequest().getURI().getPath();
        return properties.getWhitelist().stream().anyMatch(entry -> match(entry, method, path));
    }

    private boolean isAdminPath(ServerWebExchange exchange) {
        String method = exchange.getRequest().getMethod().name();
        String path = exchange.getRequest().getURI().getPath();
        return properties.getAdminPaths().stream().anyMatch(entry -> match(entry, method, path));
    }

    private boolean match(String entry, String method, String path) {
        int idx = entry.indexOf(':');
        if (idx <= 0 || idx >= entry.length() - 1) {
            return false;
        }
        String entryMethod = entry.substring(0, idx);
        String pathPattern = entry.substring(idx + 1);
        boolean methodMatch = "*".equals(entryMethod) || entryMethod.equalsIgnoreCase(method);
        return methodMatch && pathMatcher.match(pathPattern, path);
    }

    private Mono<Void> forbidden(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"code\":403,\"message\":\"无权限访问\"}";
        return exchange.getResponse().writeWith(Mono.just(
                exchange.getResponse().bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8))));
    }

    /** 返回 401 JSON。 */
    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"code\":401,\"message\":\"未登录或登录已过期\"}";
        return exchange.getResponse().writeWith(Mono.just(
                exchange.getResponse().bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8))));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}