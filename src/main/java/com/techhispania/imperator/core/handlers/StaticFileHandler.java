package com.techhispania.imperator.core.handlers;

import com.sun.net.httpserver.HttpHandler;
import com.techhispania.imperator.common.utils.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

public class StaticFileHandler implements HttpHandler {

	private static final Logger logger = LogManager.getLogger(StaticFileHandler.class);
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String requestPath = exchange.getRequestURI().getPath();

		if (requestPath.startsWith("/")) {
			requestPath = requestPath.substring(1);
		}

		InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(requestPath);
		if (resourceStream == null) {
			logger.error("Resource not found: {}", requestPath);
			exchange.sendResponseHeaders(404, -1);
			return;
		}

		String contentType = guessMimeType(requestPath);
		exchange.getResponseHeaders().add(Constants.HEADER_CONTENT_TYPE, contentType);

		byte[] bytes = resourceStream.readAllBytes();
		exchange.sendResponseHeaders(200, bytes.length);

		OutputStream os = exchange.getResponseBody();
		os.write(bytes);
		os.close();
	}

	private String guessMimeType(String path) {
		if (path.endsWith(".css"))
			return "text/css";
		if (path.endsWith(".js"))
			return "application/javascript";
		if (path.endsWith(".png"))
			return "image/png";
		if (path.endsWith(".jpg") || path.endsWith(".jpeg"))
			return "image/jpeg";
		if (path.endsWith(".webp"))
			return "image/webp";
		if (path.endsWith(".svg"))
			return "image/svg+xml";
		if (path.endsWith(".html"))
			return "text/html";

		return "application/octet-stream";
	}
}
