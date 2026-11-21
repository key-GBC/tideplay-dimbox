package com.dimbox.promotion.mq;

import com.dimbox.promotion.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeckillMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public void send(SeckillMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.SECKILL_EXCHANGE,
                RabbitConfig.SECKILL_ROUTING_KEY,
                message);
    }
}
