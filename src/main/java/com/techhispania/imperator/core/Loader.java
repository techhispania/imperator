package com.techhispania.imperator.core;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Set;

import org.reflections.Reflections;

import com.sun.net.httpserver.HttpServer;

import com.techhispania.imperator.common.annotations.Controller;
import com.techhispania.imperator.common.annotations.GetRequest;

public class Loader {

	private static final String INIT_PACKAGE = "com.techhispania.imperator";
	
	private static final int PORT = 8080;
		
	public void run() throws IOException {
		System.out.println("Loading server controllers");
		
		Reflections reflections = new Reflections(INIT_PACKAGE);
		
		Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(Controller.class);
		
		HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
		
		controllers.forEach(c -> {
			System.out.println("Loading endpoints declared in controller: " + c.getName());
			
			processEndpoints(httpServer, c);
		});
		httpServer.start();
		System.out.println("Server running on port: " + PORT);
	}
	
	private void processEndpoints(HttpServer httpServer, Class<?> classObject) {
		Method[] methods = classObject.getDeclaredMethods();
		for (Method method : methods) {
			if (method.isAnnotationPresent(GetRequest.class)) {
				GetRequest annotation = method.getAnnotation(GetRequest.class);
				String endpoint = annotation.value();
				System.out.println("Processing Get Request '" + endpoint + "' in method '" + method.getName());
									
				allowEndpoint(httpServer, endpoint, classObject, method);
			}
		}
	}
	
	private void allowEndpoint(HttpServer httpServer, String endpoint, Class<?> classObject, Method method) {
		httpServer.createContext(endpoint, exchange -> {
			System.out.println("Request received on endpoint: " + endpoint);
			method.setAccessible(true);
			try {
				Object controller = classObject.getDeclaredConstructor().newInstance();
				
				String output = (String) method.invoke(controller);
				exchange.sendResponseHeaders(200, output.length());
				exchange.getResponseBody().write(output.getBytes());
				exchange.close();
			} catch (Exception e) {
				System.out.println("Error executing method " + method.getName());
				e.printStackTrace();
			}
		});
	}
}
