package com.techhispania.imperator.server.services;

import java.util.Optional;

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
	 * @return Optional of the port found. Empty if no port is found
	 * @throws ImperatorException
	 */
	Optional<String> getServicePort(String pid) throws ImperatorException;
}
