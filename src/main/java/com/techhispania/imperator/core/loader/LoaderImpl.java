package com.techhispania.imperator.core.loader;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Headers;

import com.techhispania.imperator.common.annotations.Controller;
import com.techhispania.imperator.common.annotations.GetRequest;
import com.techhispania.imperator.common.annotations.PostRequest;
import com.techhispania.imperator.common.annotations.RequestBody;
import com.techhispania.imperator.common.utils.Constants;
import com.techhispania.imperator.core.factories.CoreFactory;
import com.techhispania.imperator.core.handlers.HandleUpload;
import com.techhispania.imperator.core.handlers.StaticFileHandler;
import com.techhispania.imperator.core.http.dto.ImperatorResponse;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.Resource;
import io.github.classgraph.ScanResult;
import tools.jackson.databind.ObjectMapper;

public class LoaderImpl implements Loader {

	private static final String CLASS_STRING = "java.lang.String";

	private static final Logger logger = LogManager.getLogger(LoaderImpl.class);
	
	private static final String INIT_PACKAGE = "com.techhispania.imperator";
	private static final String TEMPLATES_PATH = "templates";
	
	private static final int PORT = 8080;

	public void run() throws IOException {
		logger.debug("Loading server controllers");
		
		Reflections reflections = new Reflections(INIT_PACKAGE);
		
		Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(Controller.class);
		
		HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
		
		httpServer.createContext("/static", new StaticFileHandler());
		
		controllers.forEach(c -> {
			logger.debug("Loading endpoints declared in controller: " + c.getName());
			loadEndpoints(httpServer, c);
		});
		httpServer.start();
		logger.info("Server running on port: " + PORT);
	}
	
	private void loadEndpoints(HttpServer httpServer, Class<?> classObject) {
		Method[] methods = classObject.getDeclaredMethods();
		for (Method method : methods) {
			
			Optional<String> endpoint = retrieveEndpointDeclaredInMethod(method);
			
			if (endpoint.isEmpty())
				continue;
								
			runEndpoint(httpServer, endpoint.get(), classObject, method);
		}
	}
	
	private Optional<String> retrieveEndpointDeclaredInMethod(Method method) {
		Optional<String> endpoint = retrieveGetRequestsEndpoint(method);
		
		if (endpoint.isPresent())
			return endpoint;
		
		return retrievePostRequestsEndpoint(method);
	}
	
	private Optional<String> retrieveGetRequestsEndpoint(Method method) {
		if (!method.isAnnotationPresent(GetRequest.class))
			return Optional.empty();
		
		GetRequest annotation = method.getAnnotation(GetRequest.class);
		logger.debug("Loading GET Request '{}' in method '{}'",annotation.value(), method.getName());
		return Optional.of(annotation.value());
	}
	
	private Optional<String> retrievePostRequestsEndpoint(Method method) {
		if (!method.isAnnotationPresent(PostRequest.class))
			return Optional.empty();
		
		PostRequest annotation = method.getAnnotation(PostRequest.class);
		logger.debug("Loading POST Request '{}' in method '{}'", annotation.value(), method.getName());
		return Optional.of(annotation.value());
	}
	
	private void runEndpoint(HttpServer httpServer, String endpoint, Class<?> classObject, Method method) {
		httpServer.createContext(endpoint, exchange -> {
			logger.debug("Request received on endpoint: {}", endpoint);
			method.setAccessible(true); // needed to be able to execute a method using reflection
			try {
				Object controller = classObject.getDeclaredConstructor().newInstance();
				
				if (method.isAnnotationPresent(GetRequest.class)) {
					logger.debug("Processing GET request in method {}", method.getName());
					executeGetMethod(exchange, method, controller);
				} else if (method.isAnnotationPresent(PostRequest.class)) {
				    logger.debug("Processing POST request in method {}", method.getName());
				    if (!validHeaders(exchange.getRequestHeaders())) {
				    	logger.error("Invalid headers received. {}", exchange.getRequestHeaders());
				    	String errorMsg = "Invalid headers received. Review 'Content-Type' and 'Accept' headers";
				    	sendHttpResponse(exchange, 400, errorMsg);
					    return;
				    }
				    
				    if (isMultipartFormData(exchange.getRequestHeaders())) {
				    	logger.debug("Upload File request");
						byte[] bodyBytes = exchange.getRequestBody().readAllBytes();
				    	
				    	HandleUpload handleUpload = CoreFactory.createHandleUpload();
				    	Optional<String> filename = handleUpload.handleUpload(exchange, bodyBytes);
				    	logger.debug("File uploaded: {}", filename);
				    	
				    	Class<?> requestBodyType = getRequestBodyType(method);
				    	
				    	String body = new String(bodyBytes);
				    	Map<String, String> formParams = handleUpload.parseMultipartFormFields(body, handleUpload.retrieveBoundary(exchange).get());
				    	logger.debug("Form params received: {}", formParams);
				    	formParams.put("filename", filename.get());

				    	ObjectMapper mapper = new ObjectMapper();
					    Object requestObject = mapper.convertValue(formParams, requestBodyType);
				    	executePostMethod(exchange, method, controller, requestObject);
				    } else {
				    	logger.debug("REST request");
				    	String requestBody = new String(exchange.getRequestBody().readAllBytes());
				    	
					    Class<?> requestBodyType = getRequestBodyType(method);
					    
					    ObjectMapper mapper = new ObjectMapper();
					    Object requestObject = mapper.readValue(requestBody, requestBodyType);
					    
					    executePostMethod(exchange, method, controller, requestObject);
				    }
				} else {
					logger.error("Unexpected method declared. Review the annotations of method {}", method.getName());
				}
			} catch (Exception e) {
				logger.error("Error executing method {}", method.getName(), e);
			}
		});
	}
	
	private void executeGetMethod(HttpExchange exchange, Method method, Object controller) throws Exception {
		String template = (String) method.invoke(controller); // execute the method using reflection
		String html = getTemplateHtml(template);
		sendHttpResponse(exchange, 200, html);
	}
	
	private void executePostMethod(HttpExchange exchange, Method method, Object controller, Object requestObject) throws Exception {
		
		Class<?> returnType = method.getReturnType();
		
		// If the method returns an String that means that we have to redirect the user
		// in any other case, we consider that it's a REST API POST request
		if (CLASS_STRING.equals(returnType.getTypeName())) {
			String redirectPath = (String) method.invoke(controller, requestObject); // execute the method using reflection

			redirect(exchange, redirectPath);
			return;
		}
		
	    ImperatorResponse<?> response;
	    if (method.getParameterCount() == 1) {
	        response = (ImperatorResponse<?>) method.invoke(controller, requestObject); // execute the method using reflection
	    } else {
	        response = (ImperatorResponse<?>) method.invoke(controller); // execute the method using reflection
	    }
	    sendHttpResponse(exchange, response.getResponseCode(), response.toString());
	}
	
	private void redirect(HttpExchange exchange, String redirectPath) {
		try {
			exchange.getResponseHeaders().add("Location", redirectPath);
			exchange.sendResponseHeaders(303, -1); // 303 redirect, no response body
			exchange.close();
		} catch (IOException e) {
			logger.error("Error redirecting to '{}'", redirectPath, e);
		}
	}
	
	private void sendHttpResponse(HttpExchange exchange, int responseCode, String responseContent) {
		try {
		    exchange.sendResponseHeaders(responseCode, responseContent.length());
		    exchange.getResponseBody().write(responseContent.getBytes());
		    exchange.close();
		} catch (IOException e) {
			logger.error("Error sending response.", e);
		}
	}
	
	/*
	 * This method look for the method parameter annotated as @RequestBody
	 * to identify in which type we have to parse the String received in the 
	 * request body
	 */
	private Class<?> getRequestBodyType(Method method) {
	    Parameter[] parameters = method.getParameters();
	    for (Parameter parameter : parameters) {
	    	if (parameter.isAnnotationPresent(RequestBody.class)) {
	    		return parameter.getType();
	    	}
	    }
	    return null;
	} 
	
	private boolean validHeaders(Headers headers) {
		boolean valid = true;
		
		if (!headers.containsKey(Constants.HEADER_CONTENT_TYPE) || !headers.containsKey(Constants.HEADER_ACCEPT)) {
			valid = false;
		}
		
		if (!headers.getFirst(Constants.HEADER_CONTENT_TYPE).contains(Constants.APPLICATION_JSON)
				&& !headers.getFirst(Constants.HEADER_CONTENT_TYPE).contains(Constants.MULTIPART_FORM_DATA)) {
			valid = false;
		}
		
		if (!headers.getFirst(Constants.HEADER_ACCEPT).contains(Constants.APPLICATION_JSON)
				&& !headers.getFirst(Constants.HEADER_ACCEPT).contains(Constants.TEXT_HTML)
				&& !headers.getFirst(Constants.HEADER_ACCEPT).contains(Constants.APPLICATION_XHTML_XML)) {
			valid = false;	
		}
		
		return valid;
	}
	
	private boolean isMultipartFormData(Headers headers) {
		return headers.getFirst(Constants.HEADER_CONTENT_TYPE).contains(Constants.MULTIPART_FORM_DATA);
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
