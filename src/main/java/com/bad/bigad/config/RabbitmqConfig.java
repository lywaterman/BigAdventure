package com.bad.bigad.config;

import com.bad.bigad.service.ChatService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RabbitmqConfig {
    // 声明交换机和队列
    public static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    public static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    public static final String EXCHANGE_TOPICS_INFORM="exchange_topics_inform";
    public static final String ROUTINGKEY_EMAIL="inform.#.email.#";
    public static final String ROUTINGKEY_SMS="inform.#.sms.#";

    public static final String QUEUE_INFORM_COMMAND = "queue_inform_command";
    public static final String ROUTINGKEY_COMMAND = "inform.command";

    //聊天相关
    @Autowired
    ChatService chatService;

    @Autowired
    ClusterConfig clusterConfig;

    //绑定键
    public final static String msgTopicKey   = "topic.public";
    //队列
    public final static String msgTopicQueue = "topicQueue";

    @Bean
    public Queue topicQueue() {
        return new Queue(msgTopicQueue, true);
    }

//    @Bean
//    public Queue fanoutQueue() {
//        return new Queue(clusterConfig.getChatQueueName(), true);
//    }
//
//    @Bean
//    public FanoutExchange falloutExchange() {
//        return new FanoutExchange("chatFanoutExchange", true, false);
//    }

    //使用FanoutExchange来实现分布式聊天
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange("topicWebSocketExchange", true, false);
    }

    @Bean
    Binding bindingExchangeMessage() {
        return BindingBuilder.bind(topicQueue()).to(exchange()).with(msgTopicKey);
        //return BindingBuilder.bind(fanoutQueue()).to(falloutExchange());
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("127.0.0.1", 5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        //connectionFactory.setVirtualHost("bad");
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        connectionFactory.setPublisherReturns(true);
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate createRabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);

        rabbitTemplate.setMandatory(true);

        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                log.info("ConfirmCallback: "+"相关数据: "+correlationData);
                log.info("ConfirmCallback: "+"确认情况: "+ack);
                log.info("ConfirmCallback: "+"原因: "+cause);
            }
        });

        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                log.info("ReturnCallback: " + "消息：" + message);
                log.info("ReturnCallback: " + "回应码："+replyCode);
                log.info("ReturnCallback: " + "回应消息："+replyText);
                log.info("ReturnCallback: " + "交换机："+exchange);
                log.info("ReturnCallback: " + "路由键: "+routingKey);
            }
        });

        return rabbitTemplate;
    }

    @Bean
    public SimpleMessageListenerContainer messageContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
        container.setQueues(topicQueue());
        //container.setQueues(fanoutQueue());
        container.setExposeListenerChannel(true);
        container.setMaxConcurrentConsumers(1);
        container.setConcurrentConsumers(1);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setMessageListener(new ChannelAwareMessageListener() {
            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                byte[] body = message.getBody();
                String msg = new String(body);
                log.info("rabbitmq收到消息： " + msg);
                Boolean sendToWebsocket = chatService.sendMsg(msg);

                if (sendToWebsocket) {
                    log.info("消息处理成功！已经推送到websocket");
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
                }
            }
        });

        return container;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    // 声明TOPICS工作模式的交换机
    @Bean(EXCHANGE_TOPICS_INFORM)
    public Exchange EXCHANGE_TOPICS_INFORM(){
        // durable(true) 表面重启之后交换机还在
        return ExchangeBuilder.topicExchange(EXCHANGE_TOPICS_INFORM).durable(true).build();
    }

    // 声明QUEUE_INFORM_EMAIL队列
    @Bean(QUEUE_INFORM_EMAIL)
    public Queue QUEUE_INFORM_EMAIL(){
        return new Queue(QUEUE_INFORM_EMAIL);
    }
    // 声明QUEUE_INFORM_SMS队列
    @Bean(QUEUE_INFORM_SMS)
    public Queue QUEUE_INFORM_SMS(){
        return new Queue(QUEUE_INFORM_SMS);
    }

    @Bean(QUEUE_INFORM_COMMAND)
    public Queue QUEUE_INFORM_COMMAND() {
        return new Queue(QUEUE_INFORM_COMMAND);
    }

    // 交换机与QUEUE_INFORM_EMAIL队列绑定
    @Bean
    public Binding BINDING_QUEUE_INFORM_EMAIL(@Qualifier(QUEUE_INFORM_EMAIL) Queue queue,
                                              @Qualifier(EXCHANGE_TOPICS_INFORM) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTINGKEY_EMAIL).noargs();
    }
    // 交换机与QUEUE_INFORM_SMS队列绑定
    @Bean
    public Binding BINDING_QUEUE_INFORM_SMS(@Qualifier(QUEUE_INFORM_SMS) Queue queue,
                                            @Qualifier(EXCHANGE_TOPICS_INFORM) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTINGKEY_SMS).noargs();
    }

    @Bean
    public Binding BINDING_QUEUE_INFORM_COMMAND(@Qualifier(QUEUE_INFORM_COMMAND) Queue queue,
                                            @Qualifier(EXCHANGE_TOPICS_INFORM) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTINGKEY_COMMAND).noargs();
    }
}