package com.dimbox.promotion.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitConfig {

    public static final String SECKILL_EXCHANGE = "seckill.exchange";
    public static final String SECKILL_QUEUE = "seckill.queue";
    public static final String SECKILL_ROUTING_KEY = "seckill.order";

    public static final String SECKILL_DLX_EXCHANGE = "seckill.dlx.exchange";
    public static final String SECKILL_DLX_QUEUE = "seckill.dlx.queue";
    public static final String SECKILL_DLX_ROUTING_KEY = "seckill.dlx";

    @Bean
    public Queue seckillQueue() {
        return QueueBuilder.durable(SECKILL_QUEUE)
                .withArgument("x-dead-letter-exchange", SECKILL_DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", SECKILL_DLX_ROUTING_KEY)
                .build();
    }

    @Bean
    public TopicExchange seckillExchange() {
        return new TopicExchange(SECKILL_EXCHANGE);
    }

    @Bean
    public DirectExchange seckillDlxExchange() {
        return new DirectExchange(SECKILL_DLX_EXCHANGE);
    }

    @Bean
    public Queue seckillDlxQueue() {
        return new Queue(SECKILL_DLX_QUEUE, true);
    }

    @Bean
    public Binding seckillBinding() {
        return BindingBuilder.bind(seckillQueue())
                .to(seckillExchange())
                .with(SECKILL_ROUTING_KEY);
    }

    @Bean
    public Binding seckillDlxBinding() {
        return BindingBuilder.bind(seckillDlxQueue()).to(seckillDlxExchange()).with(SECKILL_DLX_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
