package com.wang.tradecenter.orderMQ;

import com.wang.fastfootstartredis.Util.JSONUtil;
import com.wang.tradecenter.entity.BO.Order;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * @Auther: wAnG
 * @Date: 2022/3/21 18:55
 * @Description:
 */

@Service
@Slf4j
public class OrderMQ {

    @Autowired
    private RocketMQTemplate mqProducer;

    private static final String MQ_TOPIC = "OrderStatue";

    public void sendLeveOne(Order order){
        String json = JSONUtil.toJsonString(order);
        Message<String> message = MessageBuilder.withPayload(json)
                .setHeader(RocketMQHeaders.KEYS, order.getOrderId())
                .build();
        SendResult sendResult = mqProducer.syncSend(MQ_TOPIC, message, 3000, 2);
        log.info("[RocketMQ] ==> result: [{}] | Topic : [{}] | \n Body : [{}]" ,sendResult.getSendStatus().name(),MQ_TOPIC,json);
    }

    public void sendLeveTwo(Order order){
        String json = JSONUtil.toJsonString(order);
        Message<String> message = MessageBuilder.withPayload(json)
                .setHeader(RocketMQHeaders.KEYS, order.getOrderId())
                .build();
        SendResult sendResult = mqProducer.syncSend(MQ_TOPIC, message, 3000, 3);
        log.info("[RocketMQ] ==> result: [{}] | Topic : [{}] | \n Body : [{}]" ,sendResult.getSendStatus().name(),MQ_TOPIC,json);
    }

    public void sendLeveThree(Order order){
        String json = JSONUtil.toJsonString(order);
        Message<String> message = MessageBuilder.withPayload(json)
                .setHeader(RocketMQHeaders.KEYS, order.getOrderId())
                .build();
        SendResult sendResult = mqProducer.syncSend(MQ_TOPIC, message, 3000, 4);
        log.info("[RocketMQ] ==> result: [{}] | Topic : [{}] | \n Body : [{}]" ,sendResult.getSendStatus().name(),MQ_TOPIC,json);
    }

}
