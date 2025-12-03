package com.techhispania.imperator.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techhispania.imperator.common.annotations.Controller;
import com.techhispania.imperator.common.annotations.GetRequest;
import com.techhispania.imperator.common.annotations.PostRequest;
import com.techhispania.imperator.common.annotations.RequestBody;
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
	public ImperatorResponse<TestPostResponse> testPost(@RequestBody TestPostRequest request) {
		logger.debug("Test Post request: {}", request);
		
		TestPostResponse response = new TestPostResponse("This is a test message");
		
		return new ImperatorResponse<TestPostResponse>(200, response);
	}
	
	// TODO remove after tests
	private static class TestPostResponse {
		
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
	
	private static class TestPostRequest {
		
		private String user;
		
		private String city;

		public TestPostRequest() {
			
		}
		
		public TestPostRequest(String user, String city) {
			this.user = user;
			this.city = city;
		}

		public String getUser() {
			return user;
		}

		public void setUser(String user) {
			this.user = user;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		@Override
		public String toString() {
			return "TestPostRequest [user=" + user + ", city=" + city + "]";
		}	
	}
}


