package com.techhispania.imperator.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techhispania.imperator.common.annotations.Controller;
import com.techhispania.imperator.common.annotations.GetRequest;
import com.techhispania.imperator.common.annotations.PostRequest;
import com.techhispania.imperator.core.http.dto.ImperatorResponse;

@Controller
public class ServerController {

	private static final Logger logger = LogManager.getLogger(ServerController.class);
	
	@GetRequest
	public String index() {
		logger.debug("Index Request");
		
		return "index";
	}
	
	@GetRequest("/health")
	public String health() {
		logger.debug("Health Request");
		
		return "health";
	}
	
	@PostRequest("/test")
	public ImperatorResponse<TestPostResponse> testPost(String request) {
		logger.debug("Test Post request: {}", request);
		
		TestPostResponse response = new TestPostResponse("This is a test message");
		
		return new ImperatorResponse<TestPostResponse>(200, response);
	}
	
	// TODO remove after tests
	private class TestPostResponse {
		
		private String message;

		public TestPostResponse(String message) {
			this.message = message;
		}
		
		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		@Override
		public String toString() {
			return "TestPostResponse [message=" + message + "]";
		}
	}
}
