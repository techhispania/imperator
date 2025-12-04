package com.techhispania.imperator.core.handlers;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

public interface HandleUpload {

	/**
	 * Method to handle the upload of files into the "deployed"
	 * directory of the imperator server
	 * 
	 * @param exchange The HttpServer exchange where the request is received
	 * @throws IOException
	 */
	void handleUpload(HttpExchange exchange) throws IOException;
}
