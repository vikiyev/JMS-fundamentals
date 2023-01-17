package com.demiglace.jms.basics;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class FirstTopic {
	public static void main(String[] args) throws Exception {
		// access JNDI initial context using jndi.properties
		InitialContext initialContext = null;
		Connection connection = null;		
		initialContext = new InitialContext();
		
		// create the connection factory
		ConnectionFactory cf = (ConnectionFactory) initialContext.lookup("ConnectionFactory");
		Topic topic = (Topic) initialContext.lookup("topic/myTopic");
		connection = cf.createConnection();
		
		Session session = connection.createSession();
		MessageProducer producer = session.createProducer(topic);
		MessageConsumer consumer1 = session.createConsumer(topic);
		MessageConsumer consumer2 = session.createConsumer(topic);
		
		// send message
		TextMessage message = session.createTextMessage("Hello World");
		producer.send(message);
		
		// receive message
		connection.start();
		TextMessage message1 = (TextMessage) consumer1.receive();
		System.out.println("Consumer 1: " + message1);
		TextMessage message2 = (TextMessage) consumer2.receive();
		System.out.println("Consumer 2: " + message2);
		
		connection.close();
		initialContext.close();
	}
}
