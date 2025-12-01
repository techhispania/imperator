package com.techhispania.imperator;

import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

public class ImperatorServer {

	private static final int PORT = 8080;
	
	public static void main(String[] args) throws Exception {
		
		System.out.println("Initializing server...");
		HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
		System.out.println("Server running on port " + PORT);
		
		httpServer.createContext("/", exchange -> {
			System.out.println("Request received");
			String html = "<h1>Test page</h1>";
			exchange.sendResponseHeaders(200, html.length());
			exchange.getResponseBody().write(html.getBytes());
			exchange.close();
		});
		
		httpServer.start();
	}
}
