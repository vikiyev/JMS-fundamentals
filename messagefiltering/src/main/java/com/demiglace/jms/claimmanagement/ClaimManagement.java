package com.demiglace.jms.claimmanagement;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

public class ClaimManagement {

	public static void main(String[] args) throws NamingException, JMSException {
		InitialContext initialContext = new InitialContext();
		Queue claimQueue = (Queue) initialContext.lookup("queue/claimQueue");

		try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
				JMSContext jmsContext = cf.createContext()) {
			// producer
			JMSProducer producer = jmsContext.createProducer();
			ObjectMessage objectMessage = jmsContext.createObjectMessage();
			
			Claim claim = new Claim();
			claim.setHospitalId(123);
			claim.setDoctorName("Doge");
			claim.setClaimAmount(1000);
			claim.setDoctorType("Vet");
			claim.setInsuranceProvider("DogeInsurance");
			objectMessage.setObject(claim);
			// set property to be filtered
			// objectMessage.setIntProperty("hospitalId", 123);
			// objectMessage.setDoubleProperty("claimAmount", 1000);
			// objectMessage.setStringProperty("doctorName", "Doge");
			objectMessage.setStringProperty("doctorType", "Vet");
			
			producer.send(claimQueue, objectMessage);
			
			// consumer
			// JMSConsumer consumer = jmsContext.createConsumer(claimQueue, "hospitalId=123");
			// JMSConsumer consumer = jmsContext.createConsumer(claimQueue, "claimAmount BETWEEN 1000 AND 5000");
			// JMSConsumer consumer = jmsContext.createConsumer(claimQueue, "doctorName LIKE 'D%'");
			JMSConsumer consumer = jmsContext.createConsumer(claimQueue, "doctorType IN ('Barker', 'Feeder') OR JMSPriority BETWEEN 3 and 6");
			Claim receiveBody = consumer.receiveBody(Claim.class);
			System.out.println(receiveBody.getClaimAmount());
		}
	}
}
