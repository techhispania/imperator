package com.techhispania.imperator.core.loader;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
					executeGetMethod(exchange, method, controller);
				} else if (method.isAnnotationPresent(PostRequest.class)) {
				    logger.debug("Processing POST request in method {}", method.getName());
				    if (!validHeaders(exchange.getRequestHeaders())) {
				    	logger.error("Invalid headers received. {}", exchange.getRequestHeaders());
				    	String errorMsg = "Invalid headers received. Review 'Content-Type' and 'Accept' headers";
					    exchange.sendResponseHeaders(400, errorMsg.length());
					    exchange.getResponseBody().write(errorMsg.getBytes());
					    exchange.close();
					    return;
				    }
				    
				    if (isMultipartFormData(exchange.getRequestHeaders())) {
				    	logger.debug("Received a file in the request");
						byte[] bodyBytes = exchange.getRequestBody().readAllBytes();
				    	
				    	HandleUpload handleUpload = CoreFactory.createHandleUpload();
				    	Optional<String> filename = handleUpload.handleUpload(exchange, bodyBytes);
				    	logger.debug("File uploaded: {}", filename);
				    	
				    	Class<?> requestBodyType = getRequestBodyType(method);
				    	
				    	String body = new String(bodyBytes);
				    	Map<String, String> formParams = parseMultipartFormFields(body, handleUpload.retrieveBoundary(exchange).get());
				    	logger.debug("Form params received: {}", formParams);
				    	formParams.put("filename", filename.get());

				    	ObjectMapper mapper = new ObjectMapper();
					    Object requestObject = mapper.convertValue(formParams, requestBodyType);
				    	executePostMethod(exchange, method, controller, requestObject);
				    } else {
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
	
	private Map<String, String> parseMultipartFormFields(String body, String boundary) {
	    Map<String, String> fields = new HashMap<>();

	    // split by boundary
	    String[] parts = body.split(Pattern.quote(boundary));

	    for (String part : parts) {
	        part = part.trim();
	        if (part.isEmpty()) continue;

	        // find header/content separator
	        int sep = part.indexOf("\r\n\r\n");
	        if (sep < 0) continue;

	        String headers = part.substring(0, sep).trim();
	        String content = part.substring(sep + 4).trim(); // remove CRLF

	        // extract name="..."
	        Matcher m = Pattern.compile("name=\"([^\"]+)\"").matcher(headers);
	        if (!m.find()) continue;

	        String fieldName = m.group(1);

	        // ignore file uploads (they contain filename="...")
	        if (headers.contains("filename=")) continue;

	        fields.put(fieldName, content);
	    }
	    return fields;
	}

	
	private void executeGetMethod(HttpExchange exchange, Method method, Object controller) throws Exception {
		String template = (String) method.invoke(controller); // execute the method using reflection
		String html = getTemplateHtml(template);
		exchange.sendResponseHeaders(200, html.length());
		exchange.getResponseBody().write(html.getBytes());
		exchange.close();
	}
	
	private void executePostMethod(HttpExchange exchange, Method method, Object controller, Object requestObject) throws Exception {
		
		Class<?> returnType = method.getReturnType();
		
		// If the method returns a template, execute it like a GET request
		// because that means, that is a POST request from a WEB FORM, and 
		// it has to be redirected to another web page
		if (returnType.getTypeName().equals("java.lang.String")) {
			String redirectPath = (String) method.invoke(controller, requestObject); // execute the method using reflection

			exchange.getResponseHeaders().add("Location", redirectPath);
			exchange.sendResponseHeaders(303, -1); // 303 redirect, no response body
			exchange.close();
			return;
		}
		
		// In any other case, execute as a REST API POST request
	    ImperatorResponse<?> response;
	    if (method.getParameterCount() == 1) {
	        response = (ImperatorResponse<?>) method.invoke(controller, requestObject); // execute the method using reflection
	    } else {
	        response = (ImperatorResponse<?>) method.invoke(controller); // execute the method using reflection
	    }

	    String json = response.toString();

	    exchange.sendResponseHeaders(response.getResponseCode(), json.length());
	    exchange.getResponseBody().write(json.getBytes());
	    exchange.close();
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
