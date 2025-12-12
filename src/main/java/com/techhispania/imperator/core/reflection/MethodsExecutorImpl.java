package com.techhispania.imperator.core.reflection;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.techhispania.imperator.core.factories.CoreFactory;
import com.techhispania.imperator.core.http.dto.ImperatorResponse;
import com.techhispania.imperator.core.templates.TemplateRender;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.Resource;
import io.github.classgraph.ScanResult;

public class MethodsExecutorImpl implements MethodsExecutor {

	private static final Logger logger = LogManager.getLogger(MethodsExecutorImpl.class);
	
	private static final String CLASS_STRING = "java.lang.String";
	
	private static final String TEMPLATES_PATH = "templates";
	
	private static ReflectionUtils reflectionUtils = CoreFactory.createReflectionUtils();
	
	public void executeGetMethod(HttpExchange exchange, Method method, Object controller) throws Exception {
		
		Map<String, Object> model = new HashMap<>();
		
		String template = null;
		if (method.getParameterCount() == 1) {
			template = (String) method.invoke(controller, model); // execute the method using reflection
		} else {
			template = (String) method.invoke(controller); // execute the method using reflection
		}
		template = getTemplatePath(template);
		String html = TemplateRender.render(template, model);
		reflectionUtils.sendHttpResponse(exchange, 200, html);
	}

	public void executePostMethod(HttpExchange exchange, Method method, Object controller, Object requestObject) throws Exception {

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
			response = (ImperatorResponse<?>) method.invoke(controller, requestObject); // execute the method using
																						// reflection
		} else {
			response = (ImperatorResponse<?>) method.invoke(controller); // execute the method using reflection
		}
		reflectionUtils.sendHttpResponse(exchange, response.getResponseCode(), response.toString());
	}
	
	private String getTemplatePath(String template) {
		try (ScanResult scanResult = new ClassGraph().acceptPaths(TEMPLATES_PATH).scan()) {
			List<String> templateFiles = scanResult.getAllResources().getPaths();

			for (String f : templateFiles) {
				String templateFileName = f.substring(TEMPLATES_PATH.length() + 1);
				logger.debug("Template: " + templateFileName);

				if (templateFileName.equalsIgnoreCase(template + ".html")) {
					logger.debug("Template is present");

					Resource resource = scanResult.getResourcesWithPath(f).get(0);

					return resource.getPath();
				}
			}
		}
		logger.debug("Template not found");
		return template;
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
}
