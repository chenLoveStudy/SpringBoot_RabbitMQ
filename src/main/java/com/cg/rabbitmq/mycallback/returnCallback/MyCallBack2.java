package com.cg.rabbitmq.mycallback.returnCallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MyCallBack2 implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnsCallback {
    /**
     * 交换机不管是否收到消息的一个回调方法
     * CorrelationData
     * 消息相关数据
     * ack
     * 交换机是否收到消息
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String id=correlationData!=null?correlationData.getId():"";
        if(ack){
            log.info("交换机已经收到 id 为:{}的消息",id);
        }else{
            log.info("交换机还未收到 id 为:{}消息,由于原因:{}",id,cause);
        }
    }
    /**
     * 有了 mandatory 参数和回退消息，我们获得了对无法投递消息的感知能力，有机会在生产者的消息
     * 无法被投递时发现并处理。
     * */
    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        String msg=new String(returnedMessage.getMessage().getBody());
        log.error(" 消 息 {}, 被 交 换 机 {} 退 回 ， 退 回 原 因 :{}, 路 由 key:{}",msg,
                returnedMessage.getExchange(),returnedMessage.getReplyText(),
                returnedMessage.getRoutingKey());
    }
}
