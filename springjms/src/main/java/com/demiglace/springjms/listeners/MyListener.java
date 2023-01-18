package com.demiglace.springjms.listeners;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class MyListener {
	
	@JmsListener(destination = "${springjms.myQueue}")
	public void receive(String message) {
		System.out.println("Message received " + message);
	}
}
