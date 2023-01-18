package com.demiglace.springjms;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.demiglace.springjms.senders.MessageSender;

@SpringBootTest
class SpringjmsApplicationTests {

	@Autowired
	MessageSender sender;
	
	@Test
	void testSendAndReceive() {
		sender.send("Hello Doge!");
	}

}
