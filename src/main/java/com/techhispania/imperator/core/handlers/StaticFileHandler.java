package com.techhispania.imperator.core.handlers;

import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;

public class StaticFileHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String requestPath = exchange.getRequestURI().getPath();

		// Convert "/static/css/styles.css" into "/static/css/styles.css"
		String resourcePath = requestPath;

		// Resources in classpath don't start with "/", so remove it
		if (resourcePath.startsWith("/")) {
			resourcePath = resourcePath.substring(1);
		}

		InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
		if (resourceStream == null) {
			exchange.sendResponseHeaders(404, -1);
			return;
		}

		// Determine MIME type
		String contentType = guessMimeType(resourcePath);
		exchange.getResponseHeaders().add("Content-Type", contentType);

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
