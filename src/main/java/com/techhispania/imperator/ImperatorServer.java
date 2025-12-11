package com.techhispania.imperator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techhispania.imperator.core.factories.CoreFactory;
import com.techhispania.imperator.core.loader.Loader;
import com.techhispania.imperator.server.infrastructure.db.DatabaseInitializer;

public class ImperatorServer {

	private static final Logger logger = LogManager.getLogger(ImperatorServer.class);
	
	public static void main(String[] args) throws Exception {
		logger.info("Initializing server...");
		
		DatabaseInitializer.runSchemaScript();
		logger.info("Database initialized");
		
		Loader loader = CoreFactory.createLoader();
		loader.run();		
	}
}
