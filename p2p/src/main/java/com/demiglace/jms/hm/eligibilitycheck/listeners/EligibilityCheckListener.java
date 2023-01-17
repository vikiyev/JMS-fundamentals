package com.demiglace.jms.hm.eligibilitycheck.listeners;

import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import com.demiglace.jms.hm.model.Patient;

public class EligibilityCheckListener implements MessageListener {
	@Override
	public void onMessage(Message message) {
		// typecast to an ObjectMessage
		ObjectMessage objectMessage = (ObjectMessage) message;
		try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
				JMSContext jmsContext = cf.createContext()) {
			InitialContext initialContext = new InitialContext();
			Queue replyQueue = (Queue) initialContext.lookup("queue/replyQueue");
			MapMessage replyMessage = jmsContext.createMapMessage();
			Patient patient = (Patient) objectMessage.getObject();
			
			// business logic
			String insuranceProvider = patient.getInsuranceProvider();
			System.out.println("Insurance provider is " + insuranceProvider);
			if (insuranceProvider.equals("dogeInsurance") || insuranceProvider.equals("cateInsurance")) {
				if (patient.getCopay()< 40 && patient.getAmountToBePaid() < 1000) {
					// eligible for the clinical service
					replyMessage.setBoolean("eligible", true);
				} else {
					replyMessage.setBoolean("eligible", false);
				}
			}
			
			// send reply back
			JMSProducer producer = jmsContext.createProducer();
			producer.send(replyQueue, replyMessage);

		} catch (JMSException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
}
