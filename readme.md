# JMS Fundamentals

Based on Bharath Thippireddy's Udemy course [JMS Fundamentals](https://www.udemy.com/course/java-message-service-jms-fundamentals/)

## Installing the Broker

After Apache ActiveMQ Artemis is downloaded, we run `artemis create pathTo/mybroker`. To run the broker, use `artemis run`. This starts the broker at port 8161. The etc/broker.xml is the configuration file.

## Terminologies

### Messaging

- The process of exchanging business data across components/applications. The message itself contains business data and may contain routing headers.
- Messaging allows heterogenous integration and loose coupling between applications that may be written in different languages
- Messaging uses asynchronous processing as well as allows multiple consumers as the load increases

### Messaging Server

- Also known as MOM (Message Oriented Middleware) is responsible for taking the incoming message and ensures that it is delivered to the receiver.
- Provides fault tolerance, load balancing, scalability, transaction management etc.
- Examples are ActiveMQ, SonicMQ, Websphere MQ, TibcoMQ

### JMS Models

#### Point to Point (p2p)

- Allows sending and receiving messages both synchronously (request/reply messaging) and asynchronously (fire and forget) through **queues**
- A message in the queue is consumed only once, and once consumed, it is removed from the queue

#### PubSub

- The messages are published into **topics** wherein there are multiple applications consuming the same message
- It is a push model wherein the messages are automatically broadcasted to the consumers without them requesting

### Session

- A unit of work in JMS

## JMS 1.x API

The JMS Provider provides us with the **ConnectionFactory** and **Destination** and puts them into the JNDI registry. We create a **Connection** from this ConnectionFactory and from the Connection we create a **Session**. Using the Session, we create **Message**, **MessageProducer**, **MessageConsumer**.

### Booting up the Project

The project uses maven-archetype-quickstart. The necessary dependencies are `artemis-jms-client-all`. We also need to create a `src/main/resources/jndi.properties file`

```properties
# Tells which the initial context will be
java.naming.factory.initial=org.apache.activemq.artemis.jndi.ActiveMQInitialContextFactory

# default location where jndi server runs
connectionFactory.ConnectionFactory=tcp://localhost:61616

# queue name, maps jndi to an object to be created dynamically at runtime
queue.queue/myQueue=myQueue
```

### Writing Messages to the Queue

The steps for sending a message to a queue in JMS 1.x are as follows:

1. Create a Connection to the JMS Server
2. Establish Session
3. Look up for the Destination
4. Send/Receive Message

```java
public class FirstQueue {
	public static void main(String[] args) {
		// access JNDI initial context using jndi.properties
		InitialContext initialContext = null;
		Connection connection = null;

		try {
			initialContext = new InitialContext();
			// retrieve connection factory from the jndi
			ConnectionFactory cf = (ConnectionFactory) initialContext.lookup("ConnectionFactory");
			// create connection
			connection = cf.createConnection();
			// create session
			Session session = connection.createSession();

			// jndi lookup
			Queue queue = (Queue) initialContext.lookup("queue/myQueue");
			// create producer
			MessageProducer producer = session.createProducer(queue);

			// send message
			TextMessage message = session.createTextMessage("Hello World");
			producer.send(message);
			System.out.println("Message sent: " + message.getText());

			// consume message
			MessageConsumer consumer = session.createConsumer(queue);
			connection.start();
			// receive the message
			TextMessage messageReceived = (TextMessage) consumer.receive(5000);
			System.out.println("Message received: " + messageReceived.getText());

		} catch (NamingException e) {
			e.printStackTrace();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		finally {
			if (initialContext !=null) {
				try {
					initialContext.close();
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (connection !=  null) {
				try {
					connection.close();
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
```

To consume a message, we must call the **Connection.start()** method.

### PubSub

For creating topics, we add the following to the jndi.properties

```properties
topic.topic/myTopic=myTopic
```

```java
public class FirstTopic {
	public static void main(String[] args) throws Exception {
		// access JNDI initial context using jndi.properties
		InitialContext initialContext = null;
		Connection connection = null;
		initialContext = new InitialContext();

		// create the connection factory
		Topic topic = (Topic) initialContext.lookup("topic/myTopic");
		ConnectionFactory cf = (ConnectionFactory) initialContext.lookup("ConnectionFactory");
		connection = cf.createConnection();

		Session session = connection.createSession();
		MessageProducer producer = session.createProducer(topic);
		MessageConsumer consumer1 = session.createConsumer(topic);
		MessageConsumer consumer2 = session.createConsumer(topic);

		// send message
		TextMessage message = session.createTextMessage("Hello World");
		producer.send(message);

		// receive message
		connection.start();
		TextMessage message1 = (TextMessage) consumer1.receive();
		System.out.println("Consumer 1: " + message1);
		TextMessage message2 = (TextMessage) consumer2.receive();
		System.out.println("Consumer 2: " + message2);

		connection.close();
		initialContext.close();
	}
}
```

## JMS 2.0

In JMS 2.0 provides a simple API and uses **JMSContext** for the Connection and Session. We don't need to define the context, create connection factories and manually close these resources. We can also create our own custom ConnectionFactory using **@JMSConnectionFactoryDefinitions**.

```java
public class JMSContextDemo {

	public static void main(String[] args) throws NamingException {
		// retrieve queue from the jndi
		InitialContext context = new InitialContext();
		Queue queue = (Queue) context.lookup("queue/myQueue");

		// create a connection factory
		try(ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
				JMSContext jmsContext = cf.createContext()) {
			// create producer and send message
			jmsContext.createProducer().send(queue, "Hello World");
			// read message
			String messageReceived = jmsContext.createConsumer(queue).receiveBody(String.class);
			System.out.println(messageReceived);
		}
	}
}
```

## JMS Message Anatomy

A JMS Message is divided into three parts:

**1. Header** - Contains metadata. There are Provider-set headers and Developer-set headers.
The Provider-set headers are:

- JMSDestination
- JMSDeliveryMode
- JMSMessageId
- JMSTimestamp
- JMSExpiration
- JMSRedelivered
- JMSPriority

The Developer-set headers are:

- JMSReplyTo
- JMSCorrelationID
- JMSType

**2. Properties**
App-Specific properties:

- setXXXProperty
- getXXXProperty

Provider-Specific properties

- JMSXUserID
- JMSXAppID
- JMSXProducerTXID
- JMSXConsumerTXID
- JMSXRcvTimestamp
- JMSDelvieryCount
- JMSXState
- JMSXGroupID
- JMSXGroupSeq

**3. Payload**

### Message Priority

Messages can be prioritized using the **JMSProducer.setPriorty()** method which takes a value from 0-9 with 9 being the highest priority. The default priority is 4.

```java
		try(ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
				JMSContext jmsContext = cf.createContext()) {
			JMSProducer producer = jmsContext.createProducer();

			String[] messages = new String[3];
			messages[0] = "Hello";
			messages[1] = "World";
			messages[2] = "Doge";

			producer.setPriority(3);
			producer.send(queue, messages[0]);

			producer.setPriority(1);
			producer.send(queue, messages[1]);

			producer.setPriority(9);
			producer.send(queue, messages[2]);

			// consumer
			JMSConsumer consumer = jmsContext.createConsumer(queue);

			for (int i = 0; i < messages.length; i++) {
				System.out.println(consumer.receiveBody(String.class));
			}
		}
```

The above will result in the following:

```
Doge
Hello
World
```

### replyTo Header

Using the replyTo header, we can dynamically specify which queue to send a reply to. We can set the header on the message itself as follows:

```java
			// create producer and send message
			JMSProducer producer = jmsContext.createProducer();
			TextMessage message = jmsContext.createTextMessage("Hello World");
			message.setJMSReplyTo(replyQueue);
			producer.send(queue, message);

			// consumer consumes the message
			JMSConsumer consumer = jmsContext.createConsumer(queue);
			TextMessage messageReceived = (TextMessage) consumer.receive();
			System.out.println(messageReceived.getText());

			JMSProducer replyProducer = jmsContext.createProducer();
			replyProducer.send(messageReceived.getJMSReplyTo(), "This is a reply");

			// reply consumer consumes the reply
			JMSConsumer replyConsumer = jmsContext.createConsumer(messageReceived.getJMSReplyTo());
			System.out.println(replyConsumer.receiveBody(String.class));
```

The replyTo() method is useful in applications wherein the queues are dynamically created.

```java
TemporaryQueue replyQueue = jmsContext.createTemporaryQueue();
```

### Linking a Request to a Reply

When multiple applications are sending requests and replies, we need to associate a particular reply to a particular reqeust. We can use the **messageIED** and **correlationID** headers for this. By default, the JMS provider (artemis) already generates ids on our messages. We can link the initial request and response to a correlationID using a hash map.

```java
			// create producer and send message
			JMSProducer producer = jmsContext.createProducer();
			TemporaryQueue replyQueue = jmsContext.createTemporaryQueue();
			TextMessage message = jmsContext.createTextMessage("Hello World");
			message.setJMSReplyTo(replyQueue);
			producer.send(queue, message);
			System.out.println(message.getJMSMessageID());

			// assign messageid to a map
			Map<String, TextMessage> requestMessages = new HashMap<>();
			requestMessages.put(message.getJMSMessageID(), message);

			// consumer consumes the message
			JMSConsumer consumer = jmsContext.createConsumer(queue);
			TextMessage messageReceived = (TextMessage) consumer.receive();
			System.out.println(messageReceived.getText());

			JMSProducer replyProducer = jmsContext.createProducer();
			TextMessage replyMessage = jmsContext.createTextMessage("This is a reply");
			replyMessage.setJMSCorrelationID(messageReceived.getJMSMessageID());
			replyProducer.send(messageReceived.getJMSReplyTo(), replyMessage);

			// reply consumer consumes the reply
			JMSConsumer replyConsumer = jmsContext.createConsumer(messageReceived.getJMSReplyTo());
			TextMessage replyReceived = (TextMessage) replyConsumer.receive();
			System.out.println(replyReceived.getJMSCorrelationID());
			System.out.println(requestMessages.get(replyReceived.getJMSCorrelationID()).getText());
```

```java
ID:a679dfe7-9692-11ed-86aa-d8f88325b949
Hello World
ID:a679dfe7-9692-11ed-86aa-d8f88325b949
Hello World
```

### Expiry Time

The setTimeToLive() can be used to set an expiry time on a message.

```java
	public static void main(String[] args) throws NamingException, InterruptedException {
		// retrieve queue from the jndi
		InitialContext context = new InitialContext();
		Queue queue = (Queue) context.lookup("queue/myQueue");
		Queue expiryQueue = (Queue) context.lookup("queue/expiryQueue");

		// create a connection factory
		try(ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
				JMSContext jmsContext = cf.createContext()) {
			// create producer and send message
			JMSProducer producer = jmsContext.createProducer();
			producer.setTimeToLive(2000);
			producer.send(queue, "Hello World");
			Thread.sleep(5000);

			// read message
			Message messageReceived = jmsContext.createConsumer(queue).receive(5000);
			System.out.println(messageReceived);

			// consume message from expiry queue
			System.out.println(jmsContext.createConsumer(expiryQueue).receiveBody(String.class));
		}
	}
```

```properties
queue.queue/expiryQueue=expiryQueue
```

### Adding Custom Message Properties

We can manually set message properties using the setXXXProperty methods.

```java
			JMSProducer producer = jmsContext.createProducer();
			TextMessage textMessage = jmsContext.createTextMessage("Hello World");
			textMessage.setBooleanProperty("loggedIn", true);
			textMessage.setStringProperty("userToken", "abc123");
			producer.send(queue, textMessage);

			// read message
			Message messageReceived = jmsContext.createConsumer(queue).receive(5000);
			System.out.println(messageReceived.getBooleanProperty("loggedIn"));
			System.out.println(messageReceived.getStringProperty("userToken"));
```

### Message Types

- TextMessage
- ByteMessage
- ObjectMessage
- StreamMessage
- MapMessage

```java
			JMSProducer producer = jmsContext.createProducer();
			BytesMessage bytesMessage = jmsContext.createBytesMessage();
			bytesMessage.writeUTF("Doge");
			bytesMessage.writeLong(123L);
			producer.send(queue, bytesMessage);

			// read message
			BytesMessage messageReceived = (BytesMessage) jmsContext.createConsumer(queue).receive(5000);
			System.out.println(messageReceived.readUTF());
			System.out.println(messageReceived.readLong());
```

Using JMS 2.0, it is even easier to send a message.

```java
			JMSProducer producer = jmsContext.createProducer();
			ObjectMessage objectMessage = jmsContext.createObjectMessage();
			Patient patient = new Patient();
			patient.setId(123);
			patient.setName("doge");
			objectMessage.setObject(patient);

			producer.send(queue, patient);

      Patient patientReceived = jmsContext.createConsumer(queue).receiveBody(Patient.class);
			System.out.println(patientReceived);
```
