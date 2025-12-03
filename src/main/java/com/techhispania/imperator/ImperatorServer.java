package com.techhispania.imperator;

import com.techhispania.imperator.core.Loader;

public class ImperatorServer {

	public static void main(String[] args) throws Exception {
		System.out.println("Initializing server...");
		
		Loader loader = new Loader();
		loader.run();		
	}
}
