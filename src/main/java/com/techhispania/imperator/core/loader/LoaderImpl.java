package com.techhispania.imperator.core.loader;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;

import com.sun.net.httpserver.HttpServer;

import com.techhispania.imperator.common.annotations.Controller;
import com.techhispania.imperator.common.annotations.GetRequest;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.Resource;
import io.github.classgraph.ScanResult;

public class LoaderImpl implements Loader {

	private static final Logger logger = LogManager.getLogger(LoaderImpl.class);
	
	private static final String INIT_PACKAGE = "com.techhispania.imperator";
	private static final String TEMPLATES_PATH = "templates";
	
	private static final int PORT = 8080;
		
	public void run() throws IOException {
		logger.debug("Loading server controllers");
		
		Reflections reflections = new Reflections(INIT_PACKAGE);
		
		Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(Controller.class);
		
		HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
		
		controllers.forEach(c -> {
			logger.debug("Loading endpoints declared in controller: " + c.getName());
			
			processEndpoints(httpServer, c);
		});
		httpServer.start();
		logger.info("Server running on port: " + PORT);
	}
	
	private void processEndpoints(HttpServer httpServer, Class<?> classObject) {
		Method[] methods = classObject.getDeclaredMethods();
		for (Method method : methods) {
			if (method.isAnnotationPresent(GetRequest.class)) {
				GetRequest annotation = method.getAnnotation(GetRequest.class);
				String endpoint = annotation.value();
				logger.debug("Processing Get Request '" + endpoint + "' in method '" + method.getName());
									
				allowEndpoint(httpServer, endpoint, classObject, method);
			}
		}
	}
	
	private void allowEndpoint(HttpServer httpServer, String endpoint, Class<?> classObject, Method method) {
		httpServer.createContext(endpoint, exchange -> {
			logger.debug("Request received on endpoint: " + endpoint);
			method.setAccessible(true); // needed to be able to execute a method using reflection
			try {
				Object controller = classObject.getDeclaredConstructor().newInstance();
				
				String template = (String) method.invoke(controller); // execute the method using reflection
				String html = getTemplateHtml(template);
				exchange.sendResponseHeaders(200, html.length());
				exchange.getResponseBody().write(html.getBytes());
				exchange.close();
			} catch (Exception e) {
				logger.error("Error executing method {}", method.getName(), e);
			}
		});
	}
	
	private String getTemplateHtml(String template) {
		try (ScanResult scanResult = new ClassGraph().acceptPaths(TEMPLATES_PATH).scan()) {
			List<String> templateFiles = scanResult.getAllResources().getPaths();
			
			for (String f : templateFiles) {
				String templateFileName = f.substring(TEMPLATES_PATH.length() + 1);
				logger.debug("Template: " + templateFileName);
				
				if (templateFileName.equalsIgnoreCase(template + ".html")) {
					logger.debug("Template is present");
					
					Resource resource = scanResult.getResourcesWithPath(f).get(0); 
					
					try {
						return new String(resource.load(), StandardCharsets.UTF_8);
					} catch (IOException e) {
						logger.error("Error reading content from template", e);
					}
				}
			}
		}
		logger.debug("Template not found");
		return template;
	}
}
