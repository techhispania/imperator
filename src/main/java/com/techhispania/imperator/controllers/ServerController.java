package com.techhispania.imperator.controllers;

import com.techhispania.imperator.common.annotations.Controller;
import com.techhispania.imperator.common.annotations.GetRequest;

@Controller
public class ServerController {

	@GetRequest
	public String index() {
		System.out.println("Index Request");
		
		return "<h1>Index Page</h1>";
	}
	
	@GetRequest("/health")
	public String health() {
		System.out.println("Health Request");
		
		return "<h1>Health Page</h1>";
	}
}
