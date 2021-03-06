package com.bad.bigad.component;

import com.bad.bigad.config.RabbitmqConfig;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component("rabbitTopicConsumer")
public class RabbitTopicConsumer {
    //单线程处理
    @RabbitListener(queues = RabbitmqConfig.QUEUE_INFORM_COMMAND)
    public void onMessage(Message message, Channel channel) throws Exception {
        log.info(new String(message.getBody(), message.getMessageProperties().getContentEncoding()));
    }
}
