package com.techhispania.imperator.core.handlers;

import java.io.IOException;
import java.util.Map;
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
	
	/**
	 * This method retrieve the web form parameters received in the 
	 * POST requests with multipart/form-data Content-Type.
	 * 
	 * @param body The string that contains the full body request received
	 * @param boundary The boundary used to split the body in different parts
	 * @return Map that contains all the parameters retrieved
	 */
	Map<String, String> parseMultipartFormFields(String body, String boundary);
	
	/**
	 * Retrieve the "boundary" from Content-Type header.
	 * This boundary is used as delimiter in the body to identify
	 * the headers, body and other data inside the request body
	 * 
	 * @param exchange The HttpExchange of the request
	 * @return The boundary found. Empty if no boundary found
	 */
	Optional<String> retrieveBoundary(HttpExchange exchange);
}
