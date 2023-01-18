package com.demiglace.jms.hr;

import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import com.demiglace.jms.hr.Employee;


public class HRApp {

	public static void main(String[] args) throws NamingException {
		InitialContext context = new InitialContext();
		Topic topic = (Topic) context.lookup("topic/empTopic");
		
		try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
				JMSContext jmsContext = cf.createContext()) {
				
				Employee employee = new Employee();
				employee.setId(123);
				employee.setFirstName("Doge");
				employee.setLastName("Cate");
				employee.setEmail("Doge@doge.com");
				employee.setDesignation("CEO");
				employee.setPhone("123456");
				
				// publish message
				for (int i = 0; i < 10; i++) {
					jmsContext.createProducer().send(topic, employee);					
				}
				System.out.println("Message sent");
			}
	}

}
