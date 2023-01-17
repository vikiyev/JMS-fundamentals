package com.demiglace.jms.messagestructure;

import javax.jms.BytesMessage;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

public class MessageTypesDemo {

	public static void main(String[] args) throws NamingException, InterruptedException, JMSException {
		// retrieve queue from the jndi
		InitialContext context = new InitialContext();
		Queue queue = (Queue) context.lookup("queue/myQueue");

		// create a connection factory
		try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
				JMSContext jmsContext = cf.createContext()) {
			// create producer and send message
			JMSProducer producer = jmsContext.createProducer();
			BytesMessage bytesMessage = jmsContext.createBytesMessage();
			bytesMessage.writeUTF("Doge");
			bytesMessage.writeLong(123L);

			StreamMessage streamMessage = jmsContext.createStreamMessage();
			streamMessage.writeBoolean(true);
			streamMessage.writeFloat(2.5f);

			MapMessage mapMessage = jmsContext.createMapMessage();
			mapMessage.setBoolean("isValid", true);

			ObjectMessage objectMessage = jmsContext.createObjectMessage();
			Patient patient = new Patient();
			patient.setId(123);
			patient.setName("doge");
			objectMessage.setObject(patient);
			
			producer.send(queue, patient);

			// read message
//			BytesMessage messageReceived = (BytesMessage) jmsContext.createConsumer(queue).receive(5000);
//			System.out.println(messageReceived.readUTF());
//			System.out.println(messageReceived.readLong());
//
//			StreamMessage strmMsg = (StreamMessage) jmsContext.createConsumer(queue).receive(5000);
//			System.out.println(strmMsg.readBoolean());
//			System.out.println(strmMsg.readFloat());
//
//			MapMessage mapMsg = (MapMessage) jmsContext.createConsumer(queue).receive(3000);
//			System.out.println(mapMsg.getBoolean("isValid"));
			
			Patient patientReceived = jmsContext.createConsumer(queue).receiveBody(Patient.class);			
			System.out.println(patientReceived);
		}
	}
}
