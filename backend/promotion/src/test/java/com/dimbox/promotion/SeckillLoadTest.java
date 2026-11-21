package com.dimbox.promotion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SeckillLoadTest {

    private static final String BASE = "http://localhost:8080/api";
    private static final int USER_COUNT = 50;      // 并发用户数
    private static final int STOCK = 10;           // 秒杀库存，必须和活动一致
    private static final Long ACTIVITY_ID = 2121897559070973953L;  // TODO 改成活动 id

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        List<String> tokens = new ArrayList<>();

        // 1. 批量注册 + 登录，拿到 50 个 token
        for (int i = 1; i <= USER_COUNT; i++) {
            String username = "loaduser" + i;
            register(client, username);        // 已存在就忽略
            tokens.add(login(client, username));
        }
        System.out.println("已准备 " + tokens.size() + " 个用户 token");

        // 2. 并发抢购
        ExecutorService pool = Executors.newFixedThreadPool(USER_COUNT);
        CountDownLatch ready = new CountDownLatch(USER_COUNT);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger success = new AtomicInteger();
        AtomicInteger reject = new AtomicInteger();
        AtomicInteger error = new AtomicInteger();

        for (String token : tokens) {
            pool.submit(() -> {
                ready.countDown();
                try {
                    start.await();   // 等所有线程就绪，一起放行
                    int code = buy(client, token);
                    if (code == 200) success.incrementAndGet();
                    else if (code == 409) reject.incrementAndGet();
                    else error.incrementAndGet();
                } catch (Exception e) {
                    error.incrementAndGet();
                }
            });
        }
        ready.await();
        start.countDown();    // 同时发起
        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.SECONDS);

        // 3. 统计
        System.out.println("成功=" + success.get()
                + "，库存不足/已抢购=" + reject.get()
                + "，异常=" + error.get());
        if (success.get() > STOCK) {
            System.out.println("!!! 超卖了");
        } else {
            System.out.println("OK：成功数 " + success.get() + " <= 库存 " + STOCK + "，未超卖");
        }
    }

    private static void register(HttpClient client, String username) throws Exception {
        String body = String.format("{\"username\":\"%s\",\"password\":\"123456\"}", username);
        send(client, BASE + "/user/register", "POST", body, null);
    }

    private static String login(HttpClient client, String username) throws Exception {
        String body = String.format("{\"username\":\"%s\",\"password\":\"123456\"}", username);
        String resp = send(client, BASE + "/user/login", "POST", body, null);
        JsonNode node = MAPPER.readTree(resp);
        return node.path("data").path("token").asText();
    }

    private static int buy(HttpClient client, String token) throws Exception {
        String resp = send(client, BASE + "/promotion/seckill/" + ACTIVITY_ID + "/buy",
                "POST", "", token);
        JsonNode node = MAPPER.readTree(resp);
        return node.path("code").asInt();
    }

    private static String send(HttpClient client, String url, String method, String body, String token)
            throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json");
        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }
        HttpRequest request = builder
                .method(method, body == null || body.isEmpty()
                        ? HttpRequest.BodyPublishers.noBody()
                        : HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}