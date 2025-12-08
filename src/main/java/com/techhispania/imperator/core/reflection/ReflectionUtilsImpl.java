package com.techhispania.imperator.core.reflection;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

public class ReflectionUtilsImpl implements ReflectionUtils {

	private static final Logger logger = LogManager.getLogger(ReflectionUtilsImpl.class);
	
	public void sendHttpResponse(HttpExchange exchange, int responseCode, String responseContent) {
		try {
			exchange.sendResponseHeaders(responseCode, responseContent.length());
			exchange.getResponseBody().write(responseContent.getBytes());
			exchange.close();
		} catch (IOException e) {
			logger.error("Error sending response.", e);
		}
	}
}
