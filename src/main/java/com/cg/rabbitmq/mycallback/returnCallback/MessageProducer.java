package com.cg.rabbitmq.mycallback.returnCallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("mandatory")
public class MessageProducer {
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    MyCallBack2 myCallBack2;
    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(myCallBack2);
        /**
         * true：
         * 交换机无法将消息进行路由时，会将该消息返回给生产者
         * false：
         * 如果发现消息无法进行路由，则直接丢弃
         */
        /**
         * 有了 mandatory 参数和回退消息，我们获得了对无法投递消息的感知能力，有机会在生产者的消息
         * 无法被投递时发现并处理。
         * */
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnsCallback(myCallBack2);
    }
    @GetMapping("sendMessage/{message}")
    public void sendMessage(@PathVariable String message){
//让消息绑定一个 id 值
        CorrelationData correlationData1 = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend("confirm.exchange","key1",message+"key1",correlationData1)
        ;
        log.info("发送消息 id 为:{}内容为{}",correlationData1.getId(),message+"key1");
        CorrelationData correlationData2 = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend("confirm.exchange","key2",message+"key2",correlationData2)
        ;
        log.info("发送消息 id 为:{}内容为{}",correlationData2.getId(),message+"key2");
    }

}
