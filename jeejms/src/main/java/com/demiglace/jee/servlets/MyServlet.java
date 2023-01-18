package com.demiglace.jee.servlets;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.demiglace.jee.jms.MyMessageProducer;

@WebServlet(urlPatterns = "/")
public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@EJB
	MyMessageProducer producer;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String message = "Hello Doge!";
		producer.sendMessage(message);
		resp.getWriter().write("Published the message: " + message);
	}
}
