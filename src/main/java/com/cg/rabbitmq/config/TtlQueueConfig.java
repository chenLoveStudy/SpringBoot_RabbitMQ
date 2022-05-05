package com.cg.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class TtlQueueConfig {
//    @Autowired
//    private RabbitTemplate rabbitTemplate;


    public static final String X_EXCHANGE = "X";
    public static final String QUEEN_A = "QA";
    public static final String QUEEN_B = "QB";
    public static final String Y_EXCHANGE = "Y";
    public static final String QUEEN_D = "QD";

    @Bean
    public DirectExchange x_exchange() {
       return new DirectExchange(X_EXCHANGE);
    }
    @Bean
    public DirectExchange y_exchange() {
        return new DirectExchange(Y_EXCHANGE);
    }
    //声明队列 A ttl 为 10s 并绑定到对应的死信交换机
    @Bean
    public Queue qa() {
        Map<String, Object> args = new HashMap<>(3);
        //声明当前队列绑定的死信交换机
        args.put("x-dead-letter-exchange", Y_EXCHANGE);
        //声明当前队列的死信路由 key
        args.put("x-dead-letter-routing-key", "YD");
        //声明队列的 TTL
        args.put("x-message-ttl", 10000);
        return QueueBuilder.durable(QUEEN_A).withArguments(args).build();
    }
    //声明队列 B ttl 为 40s 并绑定到对应的死信交换机
    @Bean
    public Queue qb() {
        Map<String, Object> args = new HashMap<>(3);
//声明当前队列绑定的死信交换机
        args.put("x-dead-letter-exchange", Y_EXCHANGE);
//声明当前队列的死信路由 key
        args.put("x-dead-letter-routing-key", "YD");
//声明队列的 TTL
        args.put("x-message-ttl", 40000);
      return  QueueBuilder.durable(QUEEN_B).withArguments(args).build();
    }
    @Bean
    public Queue qd() {
        return  new Queue(QUEEN_D);
    }

//    QA队列与X交换机绑定
    @Bean
    public Binding qaBindXchange() {
        return BindingBuilder.bind(qa()).to(x_exchange()).with("XA");
    }
    //    QB队列与X交换机绑定
    @Bean
    public Binding qbBindXchange() {
        return BindingBuilder.bind(qb()).to(x_exchange()).with("XB");
    }
    //    QD队列与Y交换机绑定
    @Bean
    public Binding qdBindYchange() {
        return BindingBuilder.bind(qd()).to(y_exchange()).with("YD");
    }

//    @Bean
    public RabbitAdmin creaatRabbitAdmin(RabbitTemplate rabbitTemplate) {
        /**
         交换机和队列和绑定 的Bean实例在  RabbitAdmin方法声明时才会创建(因为这时候才有rabbitMQ的连接，
         才可以创建)
         * */
        RabbitAdmin rabbitAdmin = new RabbitAdmin(rabbitTemplate);
        rabbitAdmin.declareExchange(x_exchange());
        rabbitAdmin.declareExchange(y_exchange());
        rabbitAdmin.declareQueue(qa());
        rabbitAdmin.declareQueue(qb());
        rabbitAdmin.declareQueue(qd());
        rabbitAdmin.declareBinding(qaBindXchange());
        rabbitAdmin.declareBinding(qbBindXchange());
        rabbitAdmin.declareBinding(qdBindYchange());
        return rabbitAdmin;
    }


}
