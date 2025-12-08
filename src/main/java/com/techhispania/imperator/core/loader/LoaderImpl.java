package com.techhispania.imperator.core.loader;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.Headers;

import com.techhispania.imperator.common.annotations.Controller;
import com.techhispania.imperator.common.annotations.GetRequest;
import com.techhispania.imperator.common.annotations.PostRequest;
import com.techhispania.imperator.common.annotations.RequestBody;
import com.techhispania.imperator.common.utils.Constants;
import com.techhispania.imperator.core.factories.CoreFactory;
import com.techhispania.imperator.core.handlers.HandleFormParams;
import com.techhispania.imperator.core.handlers.StaticFileHandler;
import com.techhispania.imperator.core.reflection.MethodsExecutor;
import com.techhispania.imperator.core.reflection.ReflectionUtils;

import tools.jackson.databind.ObjectMapper;

public class LoaderImpl implements Loader {

	private static final Logger logger = LogManager.getLogger(LoaderImpl.class);

	private static final String INIT_PACKAGE = "com.techhispania.imperator";

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
		logger.debug("Loading GET Request '{}' in method '{}'", annotation.value(), method.getName());
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
		MethodsExecutor methodsExecutor = CoreFactory.createMethodsExecutor();
		ReflectionUtils reflectionUtils = CoreFactory.createReflectionUtils();
		
		httpServer.createContext(endpoint, exchange -> {
			logger.debug("Request received on endpoint: {}", endpoint);
			method.setAccessible(true); // needed to be able to execute a method using reflection
			try {
				Object controller = classObject.getDeclaredConstructor().newInstance();

				if (method.isAnnotationPresent(GetRequest.class)) {
					logger.debug("Processing GET request in method {}", method.getName());
					methodsExecutor.executeGetMethod(exchange, method, controller);
				} else if (method.isAnnotationPresent(PostRequest.class)) {
					logger.debug("Processing POST request in method {}", method.getName());
					if (!validHeaders(exchange.getRequestHeaders())) {
						logger.error("Invalid headers received. {}", exchange.getRequestHeaders());
						String errorMsg = "Invalid headers received. Review 'Content-Type' and 'Accept' headers";
						reflectionUtils.sendHttpResponse(exchange, 400, errorMsg);
						return;
					}

					Class<?> requestBodyType = getRequestBodyType(method);
					
					if (isMultipartFormData(exchange.getRequestHeaders())) {
						logger.debug("Upload File Form request");
						
						HandleFormParams handleFormParams = CoreFactory.createHandleUpload();
						handleFormParams.handle(exchange, method, controller, requestBodyType);
					} else if (isApplicationXWWWFormUrlEncoded(exchange.getRequestHeaders())) {
						logger.debug("Standard Form request");
						
						HandleFormParams handleFormParams = CoreFactory.createHandleStandardFormParams();
						handleFormParams.handle(exchange, method, controller, requestBodyType);
					} else {
						logger.debug("REST request");
						String requestBody = new String(exchange.getRequestBody().readAllBytes());

						ObjectMapper mapper = new ObjectMapper();
						Object requestObject = mapper.readValue(requestBody, requestBodyType);

						methodsExecutor.executePostMethod(exchange, method, controller, requestObject);
					}
				} else {
					logger.error("Unexpected method declared. Review the annotations of method {}", method.getName());
				}
			} catch (Exception e) {
				logger.error("Error executing method {}", method.getName(), e);
			}
		});
	}

	/*
	 * This method look for the method parameter annotated as @RequestBody to
	 * identify in which type we have to parse the String received in the request
	 * body
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
				&& !headers.getFirst(Constants.HEADER_CONTENT_TYPE).contains(Constants.MULTIPART_FORM_DATA)
				&& !headers.getFirst(Constants.HEADER_CONTENT_TYPE)
						.contains(Constants.APPLICATION_X_WWW_FORM_URLENCODED)) {
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

	private boolean isApplicationXWWWFormUrlEncoded(Headers headers) {
		return headers.getFirst(Constants.HEADER_CONTENT_TYPE).contains(Constants.APPLICATION_X_WWW_FORM_URLENCODED);
	}
}
