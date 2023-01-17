package com.demiglace.jms.messagestructure;

import java.util.Iterator;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

public class MessagePriority {

	public static void main(String[] args) throws NamingException {
		// retrieve queue from the jndi
		InitialContext context = new InitialContext();
		Queue queue = (Queue) context.lookup("queue/myQueue");
		
		try(ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
				JMSContext jmsContext = cf.createContext()) {
			JMSProducer producer = jmsContext.createProducer();
			
			String[] messages = new String[3];
			messages[0] = "Hello";
			messages[1] = "World";
			messages[2] = "Doge";
			
			producer.setPriority(3);
			producer.send(queue, messages[0]);
			
			producer.setPriority(1);
			producer.send(queue, messages[1]);
			
			producer.setPriority(9);
			producer.send(queue, messages[2]);
			
			// consumer
			JMSConsumer consumer = jmsContext.createConsumer(queue);
			
			for (int i = 0; i < messages.length; i++) {
				Message receivedMessage = consumer.receive();
				try {
					System.out.println(receivedMessage.getJMSPriority());
				} catch (JMSException e) {
					e.printStackTrace();
				}
				// System.out.println(consumer.receiveBody(String.class));
			}
		}
	}

}
