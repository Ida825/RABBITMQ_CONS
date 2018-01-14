package cn.et;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
@Controller
public class SendEmail {
	/**
	 * 序列化（对象转字节数组）
	 * @return
	 * @throws IOException 
	 */
	public byte[] seq(Object obj) throws IOException{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(obj);
		return bos.toByteArray();
	}
	
	/**
	 * 反序列化（字节数组转对象）
	 * @param bt
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object dseq(byte[] bt) throws IOException, ClassNotFoundException{
		ByteArrayInputStream bis = new ByteArrayInputStream(bt);
		ObjectInputStream ois = new ObjectInputStream(bis);	
		return ois.readObject();
		
	}
	
	
	private static final String QUEUE_NAME = "MAIL_QUEUE";
	
	@Autowired
	private  JavaMailSender jms ;
	
	@RequestMapping("/send")
	public void send() throws IOException, TimeoutException{
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("192.168.6.128");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		 //消费者也需要定义队列 有可能消费者先于生产者启动   
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		
		//定义回调抓取消息
		Consumer consumer = new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,  
                    byte[] body) throws IOException {  
				try {
					Map map = (Map)dseq(body);
					
					System.out.println(jms+"------------------------------"+map);
					
					SimpleMailMessage smm = new SimpleMailMessage();
					smm.setFrom("sunyingida@126.com");
					smm.setTo(map.get("sendTo").toString());
					smm.setSubject(map.get("subject").toString());
					smm.setText(map.get("content").toString());
					System.out.println(map+"----------");
					System.out.println(jms);
					jms.send(smm);
					
					System.out.println(map.get("content"));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
               
            }  
			
		};
		//发送邮件
		channel.basicConsume(QUEUE_NAME, true, consumer); 
		
	}
}
