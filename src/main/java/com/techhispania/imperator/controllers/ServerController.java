package com.techhispania.imperator.controllers;

import com.techhispania.imperator.common.annotations.Controller;
import com.techhispania.imperator.common.annotations.GetRequest;

@Controller
public class ServerController {

	@GetRequest
	public String index() {
		System.out.println("Index Request");
		
		return "index";
	}
	
	@GetRequest("/health")
	public String health() {
		System.out.println("Health Request");
		
		return "health";
	}
}
