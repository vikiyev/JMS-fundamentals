package com.demiglace.jms.wellness;

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

public class WellnessApp {

	public static void main(String[] args) throws NamingException, JMSException {
		InitialContext context = new InitialContext();
		Topic topic = (Topic) context.lookup("topic/empTopic");

		try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
				JMSContext jmsContext = cf.createContext()) {
			// create shared consumer
			JMSConsumer consumer = jmsContext.createSharedConsumer(topic, "sharedConsumer");
			JMSConsumer consumer2 = jmsContext.createSharedConsumer(topic, "sharedConsumer");

			for (int i = 0; i < 10; i += 2) {
				// consume message
				Message message = consumer.receive();
				Employee employee = message.getBody(Employee.class);
				System.out.println("Consumer 1 " + employee.getFirstName());

				Message message2 = consumer2.receive();
				Employee employee2 = message.getBody(Employee.class);
				System.out.println("Consumer 2 " + employee2.getFirstName());
			}

		}
	}

}
