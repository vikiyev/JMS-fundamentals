package com.demiglace.jee.jms;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Queue;

@Stateless
@LocalBean
public class MyMessageProducer {
	// inject queue
	@Resource(mappedName = "java:/queue/myQueue")
	Queue myQueue;
	
	@Inject
	JMSContext jmsContext;
	
	public void sendMessage(String message) {
		jmsContext.createProducer().send(myQueue, message);
	}
}
