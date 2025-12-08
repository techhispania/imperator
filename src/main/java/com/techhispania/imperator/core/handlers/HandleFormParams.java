package com.techhispania.imperator.core.handlers;

import java.lang.reflect.Method;

import com.sun.net.httpserver.HttpExchange;

public interface HandleFormParams {

	/**
	 * Method to handle the upload of files into the "deployed"
	 * directory of the imperator server
	 * 
	 * @param exchange The HttpServer exchange where the request is received
	 * @param bodyBytes The body received in the request
	 * @return The name of the file uploaded. Empty if no file was uploaded
	 * @throws Exception
	 */
	void handle(HttpExchange exchange, Method method, Object controller, Class<?> requestBodyType) throws Exception;
}
