package cn.itcast.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    public static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    public static final String EXCHANGE_TOPICS_INFORM="exchange_topics_inform";

    //声明交换机
    @Bean(EXCHANGE_TOPICS_INFORM)
    public Exchange setExchange(){
        return ExchangeBuilder.topicExchange(EXCHANGE_TOPICS_INFORM).durable(true).build();
    }
    //声明队列
    @Bean(QUEUE_INFORM_EMAIL)
    public Queue setMailQueue(){
        return new Queue(QUEUE_INFORM_EMAIL);
    }
    @Bean(QUEUE_INFORM_SMS)
    public Queue setSmsQueue(){
        return new Queue(QUEUE_INFORM_SMS);
    }
    //绑定交换机队列
    public Binding bingMail(@Qualifier(EXCHANGE_TOPICS_INFORM) Exchange exchange,@Qualifier(QUEUE_INFORM_EMAIL) Queue queue){
        return BindingBuilder.bind(queue).to(exchange).with("inform.#.email.#").noargs();
    }
    public Binding bingCms(@Qualifier(EXCHANGE_TOPICS_INFORM) Exchange exchange,@Qualifier(QUEUE_INFORM_SMS) Queue queue){
        return BindingBuilder.bind(queue).to(exchange).with("inform.#.cms.#").noargs();
    }
}
