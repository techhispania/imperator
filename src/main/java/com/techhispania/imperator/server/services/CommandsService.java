package com.techhispania.imperator.server.services;

import com.techhispania.imperator.common.exceptions.ImperatorException;

public interface CommandsService {

	/**
	 * Checks if a Process is running on the server
	 * 
	 * @param pid The process id
	 * @return True if is running. False if it's not running
	 * @throws ImperatorException
	 */
	boolean isProcessRunning(String pid) throws ImperatorException;
	
	/**
	 * Get the port where a microservice is running
	 * 
	 * @param pid The process id where the microservice is running
	 * @return The port found
	 * @throws ImperatorException
	 */
	String getServicePort(String pid) throws ImperatorException;
}
