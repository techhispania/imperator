package com.techhispania.imperator.core.handlers;

import java.io.IOException;
import java.util.Optional;

import com.sun.net.httpserver.HttpExchange;

public interface HandleUpload {

	/**
	 * Method to handle the upload of files into the "deployed"
	 * directory of the imperator server
	 * 
	 * @param exchange The HttpServer exchange where the request is received
	 * @param bodyBytes The body received in the request
	 * @return The name of the file uploaded. Empty if no file was uploaded
	 * @throws IOException
	 */
	Optional<String> handleUpload(HttpExchange exchange, byte[] bodyBytes) throws IOException;
	
	Optional<String> retrieveBoundary(HttpExchange exchange);
}
