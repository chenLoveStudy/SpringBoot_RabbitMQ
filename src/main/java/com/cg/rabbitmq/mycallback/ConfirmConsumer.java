package com.cg.rabbitmq.mycallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ConfirmConsumer {
    public static final String CONFIRM_QUEUE_NAME = "confirm.queue";
    public static final String WARNING_QUEUE_NAME = "warning.queue";
    public static final String BACKUP_QUEUE_NAME = "backup.queue";

    @RabbitListener(queues = CONFIRM_QUEUE_NAME)
    public void receiveMsg(Message message) {
        String msg = new String(message.getBody());
        log.info("接受到队列 confirm.queue 消息:{}", msg);
    }

    /**
     mandatory 参数与备份交换机可以一起使用的时候，如果两者同时开启，消息究竟何去何从？谁优先
     级高，http://localhost:8080/mandatory/sendMessage/备份交换机
     经过上面结果显示答案是备份交换机优先级高。
     */
    @RabbitListener(queues =WARNING_QUEUE_NAME )
    public void  warningMsg(Message message) {
        String msg = new String(message.getBody());
        log.info("报警发现不可路由消息:{},被备份交换机的报警队列所接收", msg);

    }
    @RabbitListener(queues =BACKUP_QUEUE_NAME )
    public void  backQueueMsg(Message message) {
        String msg = new String(message.getBody());
        log.info("发现不可路由消息:{},被备份交换机的备份队列所接收", msg);

    }
}