package com.cg.rabbitmq.controller;

import com.cg.rabbitmq.config.DelayedQueueConfig;
import com.cg.rabbitmq.config.TtlQueueConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("ttl")
public class SendMsgController {
    /**
     若只是在配置类中声明创建bean，
     则当生产者发送消息时才会创建交换机和队列(http://localhost:8080/ttl/sendMsg/哈哈)
     * */

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/sendMsg/{msg}")
    public void sendMsg(@PathVariable("msg") String msg) {
        log.info("当前时间：{},发送一条信息给两个 TTL 队列:{}", new Date().toString(),msg);

        rabbitTemplate.convertAndSend(TtlQueueConfig.X_EXCHANGE, "XA",
                "消息来自 ttl 为 10S 的队列: "+msg);
        rabbitTemplate.convertAndSend(TtlQueueConfig.X_EXCHANGE, "XB",
                "消息来自 ttl 为 40S 的队列: "+msg);


    }
    /**
     看起来似乎没什么问题，但是在最开始的时候，就介绍过如果使用在消息属性上设置 TTL 的方式，消
     息可能并不会按时“死亡“，因为 RabbitMQ 只会检查第一个消息是否过期，如果过期则丢到死信队列，
     如果第一个消息的延时时长很长，而第二个消息的延时时长很短，第二个消息并不会优先得到执行。
     */
    @GetMapping("sendExpirationMsg/{message}/{ttlTime}")
    public void sendMsg(@PathVariable("message") String message,@PathVariable("ttlTime") String ttlTime) {
//        CorrelationData
        MessagePostProcessor messagePostProcessor=(e)->{
            e.getMessageProperties().setExpiration(ttlTime);
            return e;
        };

        rabbitTemplate.convertAndSend("X", "XC", message,messagePostProcessor );
        log.info("当前时间：{},发送一条时长{}毫秒 TTL 信息给队列 C:{}", new Date(), ttlTime, message);
    }
//    用延时队列发信息测试
    @GetMapping("sendDelayMsg/{message}/{delayTime}")
    public void sendMsg(@PathVariable String message,@PathVariable Integer delayTime) {
        MessagePostProcessor messagePostProcessor=e->{
            e.getMessageProperties().setDelay(delayTime);
            return e;
        };
        rabbitTemplate.convertAndSend(DelayedQueueConfig.DELAYED_EXCHANGE_NAME, DelayedQueueConfig.DELAYED_ROUTING_KEY,
                message, messagePostProcessor);
        log.info(" 当 前 时 间 ： {}, 发 送 一 条 延 迟 {} 毫秒的信息给队列 delayed.queue:{}", new
                Date(),delayTime, message);
    }

}
