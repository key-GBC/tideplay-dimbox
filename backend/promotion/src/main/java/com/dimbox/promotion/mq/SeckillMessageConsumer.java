package com.dimbox.promotion.mq;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dimbox.promotion.client.ProductClient;
import com.dimbox.promotion.client.StockOperateRequest;
import com.dimbox.promotion.config.RabbitConfig;
import com.dimbox.promotion.entity.SeckillOrder;
import com.dimbox.promotion.mapper.SeckillOrderMapper;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SeckillMessageConsumer {

    private final SeckillOrderMapper seckillOrderMapper;
    private final ProductClient productClient;

    @RabbitListener(queues = RabbitConfig.SECKILL_QUEUE)
    public void onMessage(SeckillMessage message,
                          Channel channel,
                          @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        SeckillOrder existing = seckillOrderMapper.selectOne(
                new LambdaQueryWrapper<SeckillOrder>()
                        .eq(SeckillOrder::getActivityId, message.getActivityId())
                        .eq(SeckillOrder::getUserId, message.getUserId()));

        if(existing != null && existing.getStatus() != null && existing.getStatus() == 1) {
            channel.basicAck(deliveryTag, false);
            return;
        }

        if (existing == null) {
            SeckillOrder order = buildOrder(message);
            order.setStatus(0);
            try {
                seckillOrderMapper.insert(order);
                existing = order;
            } catch (DuplicateKeyException e) {
                existing = seckillOrderMapper.selectOne(
                        new LambdaQueryWrapper<SeckillOrder>()
                                .eq(SeckillOrder::getActivityId, message.getActivityId())
                                .eq(SeckillOrder::getUserId, message.getUserId()));
                if (existing != null && existing.getStatus() != null && existing.getStatus() == 1) {
                    channel.basicAck(deliveryTag, false);   // 别人已经扣完了，确认
                    return;
                }
            }
        }

        try {
            productClient.deduct(new StockOperateRequest(message.getSkuId(), 1));

            SeckillOrder update = new SeckillOrder();
            update.setId(existing.getId());
            update.setStatus(1);
            seckillOrderMapper.updateById(update);

            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            channel.basicNack(deliveryTag, false, false);
        }

    }

    private SeckillOrder buildOrder(SeckillMessage message) {
        SeckillOrder order = new SeckillOrder();
        order.setActivityId(message.getActivityId());
        order.setUserId(message.getUserId());
        order.setSkuId(message.getSkuId());
        order.setOrderNo(message.getOrderNo());
        return order;
    }
}
