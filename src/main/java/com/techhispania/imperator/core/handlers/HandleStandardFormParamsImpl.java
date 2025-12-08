package com.techhispania.imperator.core.handlers;

import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.techhispania.imperator.core.factories.CoreFactory;
import com.techhispania.imperator.core.reflection.MethodsExecutor;

import tools.jackson.databind.ObjectMapper;

public class HandleStandardFormParamsImpl implements HandleFormParams {

	private static final Logger logger = LogManager.getLogger(HandleStandardFormParamsImpl.class);
	
	@Override
	public void handle(HttpExchange exchange, Method method, Object controller, Class<?> requestBodyType) throws Exception {
		logger.debug("Form request");
		byte[] bodyBytes = exchange.getRequestBody().readAllBytes();
		String body = new String(bodyBytes);

		logger.debug("Form params received: {}", body);
		Map<String, String> params = parseParams(body);
		
		ObjectMapper mapper = new ObjectMapper();
		Object requestObject = mapper.convertValue(params, requestBodyType);
		
		MethodsExecutor methodsExecutor = CoreFactory.createMethodsExecutor();
		methodsExecutor.executePostMethod(exchange, method, controller, requestObject);
	}

	private Map<String, String> parseParams(String body) {
		Map<String, String> paramsMap = new HashMap<>();
		
		String[] params = body.split("&");
		
		for (String param : params) {
			String key = param.split("=")[0];
			String value = URLDecoder.decode(param.split("=")[1], StandardCharsets.UTF_8);
			paramsMap.put(key, value);
		}
		return paramsMap;
	}
}
