package com.demiglace.jee.jms.mdbs;

import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@MessageDriven(name = "MyMdb", activationConfig = {
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/myQueue"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
})
public class MyMdb implements MessageListener {
	private static Logger LOGGER = Logger.getLogger(MyMdb.class.toString());

	@Override
	public void onMessage(Message message) {
		if (message instanceof TextMessage) {
			try {
				// typecast and get the message
				String text = ((TextMessage) message).getText();
				LOGGER.info("Received message is: " + text);
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}
}
