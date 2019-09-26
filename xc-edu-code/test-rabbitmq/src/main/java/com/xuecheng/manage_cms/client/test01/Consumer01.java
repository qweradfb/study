package com.xuecheng.manage_cms.client.test01;

import com.rabbitmq.client.*;

import java.io.IOException;

public class Consumer01 {
    private static final String QUEUE = "helloworld";

    public static void main(String[] args) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE,true,false,false,null);
        DefaultConsumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                super.handleDelivery(consumerTag, envelope, properties, body);
                String exchange = envelope.getExchange();
                String routingkey = envelope.getRoutingKey();
                long deliveryTag = envelope.getDeliveryTag();
                String msg = new String(body,"utf-8");
                System.out.println("receive message.."+msg);
            }
        };
        channel.basicConsume(QUEUE,true,consumer);

    }
}
