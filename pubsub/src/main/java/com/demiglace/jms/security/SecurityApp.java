package com.demiglace.jms.security;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import com.demiglace.jms.hr.Employee;


public class SecurityApp {

	public static void main(String[] args) throws NamingException, JMSException, InterruptedException {
		InitialContext context = new InitialContext();
		Topic topic = (Topic) context.lookup("topic/empTopic");
		
		try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
				JMSContext jmsContext = cf.createContext()) {
				// durable subscription
				jmsContext.setClientID("securityApp");
				// create durable consumer
				JMSConsumer consumer = jmsContext.createDurableConsumer(topic, "subscription1");
				consumer.close(); // simulate application downtime
				Thread.sleep(10000);
				consumer = jmsContext.createDurableConsumer(topic, "subscription1"); // simulate app restart
				
				// consume message
				Message message = consumer.receive();
				Employee employee = message.getBody(Employee.class);
				System.out.println(employee.getFirstName());
				
				consumer.close();
				jmsContext.unsubscribe("subscription1");
			}
	}

}
