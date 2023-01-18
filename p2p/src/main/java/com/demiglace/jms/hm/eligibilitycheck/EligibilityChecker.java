package com.demiglace.jms.hm.eligibilitycheck;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import com.demiglace.jms.hm.eligibilitycheck.listeners.EligibilityCheckListener;

public class EligibilityChecker {
	public static void main(String[] args) throws NamingException, InterruptedException {
		InitialContext initialContext = new InitialContext();
		Queue requestQueue = (Queue) initialContext.lookup("queue/requestQueue");

		try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
				JMSContext jmsContext = cf.createContext("eligibilityuser", "eligibilitypass")) {
			JMSConsumer consumer1 = jmsContext.createConsumer(requestQueue);
			JMSConsumer consumer2 = jmsContext.createConsumer(requestQueue);
			
			for (int i = 0; i < 10; i+=2) { // using two consumers, iterate 5 times
				System.out.println("Consumer 1: " + consumer1.receive());
				System.out.println("Consumer 2: " + consumer2.receive());
			}
			
			// listen for messages async
			// consumer.setMessageListener(new EligibilityCheckListener());
			// Thread.sleep(10000); // wait for ClinicalsApp to send data
		}
	}
}
