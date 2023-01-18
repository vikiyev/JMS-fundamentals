package com.demiglace.jms.payroll;

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


public class PayrollApp {

	public static void main(String[] args) throws NamingException, JMSException {
		InitialContext context = new InitialContext();
		Topic topic = (Topic) context.lookup("topic/empTopic");
		
		try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
				JMSContext jmsContext = cf.createContext()) {
				// create consumer
				JMSConsumer consumer = jmsContext.createConsumer(topic);
				// consume message
				Message message = consumer.receive();
				Employee employee = message.getBody(Employee.class);
				System.out.println(employee.getFirstName());
			}
	}

}
