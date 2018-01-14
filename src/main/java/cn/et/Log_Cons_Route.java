package cn.et;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class Log_Cons_Route {
	private static final String EXCHANGE_NAME = "amq_log_route";

	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("192.168.6.128");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();	
		channel.exchangeDeclare(EXCHANGE_NAME,"direct", true);
		//定义一个随机的队列 用于交换器获取消息
		String queueName = channel.queueDeclare().getQueue();
		//指定接收哪些消息
		channel.queueBind(queueName, EXCHANGE_NAME, "info");
		Consumer consumer = new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,  
                    byte[] body) throws IOException {  
                System.out.println(new String(body, "UTF-8"));  
                
            }  
			
		};
		channel.basicConsume(queueName, false,consumer);
		
	}

}
