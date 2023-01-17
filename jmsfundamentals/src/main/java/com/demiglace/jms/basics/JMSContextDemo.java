package com.demiglace.jms.basics;

import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

public class JMSContextDemo {

	public static void main(String[] args) throws NamingException {
		// retrieve queue from the jndi
		InitialContext context = new InitialContext();
		Queue queue = (Queue) context.lookup("queue/myQueue");
		
		// create a connection factory
		try(ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
				JMSContext jmsContext = cf.createContext()) {
			// create producer and send message
			jmsContext.createProducer().send(queue, "Hello World");
			// read message
			String messageReceived = jmsContext.createConsumer(queue).receiveBody(String.class);
			System.out.println(messageReceived);
		}
	}
}
