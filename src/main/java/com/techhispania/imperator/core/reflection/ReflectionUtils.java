package com.techhispania.imperator.core.reflection;

import com.sun.net.httpserver.HttpExchange;

public interface ReflectionUtils {

	void sendHttpResponse(HttpExchange exchange, int responseCode, String responseContent);
}
