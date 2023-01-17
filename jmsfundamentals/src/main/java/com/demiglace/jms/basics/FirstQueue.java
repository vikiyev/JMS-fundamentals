package com.demiglace.jms.basics;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class FirstQueue {
	public static void main(String[] args) {
		// access JNDI initial context using jndi.properties
		InitialContext initialContext = null;
		Connection connection = null;
		
		try {
			initialContext = new InitialContext();
			// retrieve connection factory from the jndi
			ConnectionFactory cf = (ConnectionFactory) initialContext.lookup("ConnectionFactory");
			// create connection
			connection = cf.createConnection();
			// create session
			Session session = connection.createSession();
			
			// jndi lookup
			Queue queue = (Queue) initialContext.lookup("queue/myQueue");
			// create producer
			MessageProducer producer = session.createProducer(queue);
			
			// send message
			TextMessage message = session.createTextMessage("Hello World");
			producer.send(message);
			System.out.println("Message sent: " + message.getText());
			
			// consume message
			MessageConsumer consumer = session.createConsumer(queue);
			connection.start();
			// receive the message
			TextMessage messageReceived = (TextMessage) consumer.receive(5000);
			System.out.println("Message received: " + messageReceived.getText());
			
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		finally {
			if (initialContext !=null) {
				try {
					initialContext.close();
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (connection !=  null) {
				try {
					connection.close();
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
