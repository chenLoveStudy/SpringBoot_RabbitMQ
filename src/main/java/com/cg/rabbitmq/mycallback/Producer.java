package com.cg.rabbitmq.mycallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

//@RestController
@Slf4j
@RequestMapping("/confirm")
public class Producer {
    public static final String CONFIRM_EXCHANGE_NAME = "confirm.exchange";
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private MyCallBack myCallBack;
    //依赖注入 rabbitTemplate 之后再设置它的回调对象
    @PostConstruct
    public void init(){
        rabbitTemplate.setConfirmCallback(myCallBack);//注入后rabbitTemplate才有交换机回调方法的功能
    }
    /**
      可以看到，发送了两条消息，第一条消息的 RoutingKey 为 "key1"，第二条消息的 RoutingKey 为
      "key2"，两条消息都成功被交换机接收，也收到了交换机的确认回调，但消费者只收到了一条消息，因为
      第二条消息的 RoutingKey 与队列的 BindingKey 不一致，也没有其它队列能接收这个消息，所有第二条
      消息被直接丢弃了。
     * */
    @GetMapping("sendMessage/{message}")
    public void sendMessage(@PathVariable String message){

        //指定消息 id 为 1
        CorrelationData correlationData1=new CorrelationData("1");
        String routingKey="key1";
        rabbitTemplate.convertAndSend(CONFIRM_EXCHANGE_NAME,routingKey,message+routingKey,
                correlationData1);
        CorrelationData correlationData2=new CorrelationData("2");
        routingKey="key2";
        rabbitTemplate.convertAndSend(CONFIRM_EXCHANGE_NAME,routingKey,message+routingKey,
                correlationData2);
        log.info("发送消息内容:{}",message);
    }
}

