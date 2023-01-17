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
