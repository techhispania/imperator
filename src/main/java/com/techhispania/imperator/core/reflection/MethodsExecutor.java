package com.techhispania.imperator.core.reflection;

import java.lang.reflect.Method;

import com.sun.net.httpserver.HttpExchange;

public interface MethodsExecutor {

	void executeGetMethod(HttpExchange exchange, Method method, Object controller) throws Exception;
	
	void executePostMethod(HttpExchange exchange, Method method, Object controller, Object requestObject) throws Exception;
}
