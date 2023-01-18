package com.demiglace.jms.hm.clinicals;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import com.demiglace.jms.hm.model.Patient;

// sends the message
public class ClinicalsApp {
	public static void main(String[] args) throws NamingException, JMSException {
		InitialContext initialContext = new InitialContext();
		Queue requestQueue = (Queue) initialContext.lookup("queue/requestQueue");
		Queue replyQueue = (Queue) initialContext.lookup("queue/replyQueue");

		try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
				JMSContext jmsContext = cf.createContext("clinicaluser", "clinicalpass")) {
			JMSProducer producer = jmsContext.createProducer();

			Patient patient = new Patient();
			ObjectMessage objectMessage = jmsContext.createObjectMessage(patient);
			patient.setId(123);
			patient.setName("doge");
			patient.setInsuranceProvider("dogeInsurance");
			patient.setCopay(30d);
			patient.setAmountToBePaid(100d);

			// send object message
			objectMessage.setObject(patient);

			for (int i = 0; i < 10; i++) {
				producer.send(requestQueue, objectMessage);
			}

			// process the response
			// JMSConsumer consumer = jmsContext.createConsumer(replyQueue);
			// MapMessage replyMessage = (MapMessage) consumer.receive(10000);
			// System.out.println("Patient eligibility is " + replyMessage.getBoolean("eligible"));
		}
	}
}
