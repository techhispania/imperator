package com.techhispania.imperator.server.services.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techhispania.imperator.common.exceptions.ImperatorException;
import com.techhispania.imperator.common.utils.Constants;
import com.techhispania.imperator.server.services.CommandsService;

public class CommandsServiceImpl implements CommandsService {

	private static final Logger logger = LogManager.getLogger(CommandsServiceImpl.class);
	
	@Override
	public boolean isProcessRunning(String pid) throws ImperatorException {
		logger.info("Checking if process '{}' is running", pid);
		boolean isRunning = true;

		String command = String.format("ps -ef | grep %s | grep -v \"grep\" | awk -F ' ' '{print $2}'", pid);
		
		try {
			Process process = executeCommand(command);
			
			String output = readCommandOutput(process);
			if (output == null || output.length() == 0)
				isRunning = false;
		} catch (IOException e) {
			String errorMsg = String.format("Error checking if process '%s' is running", pid);
			logger.error(errorMsg, e);
			throw new ImperatorException(Constants.HTTP_CODE_INTERNAL_SERVER_ERROR, errorMsg);
		}
		return isRunning;
	}
	
	@Override
	public Optional<String> getServicePort(String pid) throws ImperatorException {
		logger.info("Looking for port of pid '{}'", pid);
		
		String command = String.format("netstat -pan | grep %s | grep LISTEN | awk -F ' ' '{print $4}' | awk -F ':' '{print $NF}'", pid);
		
		try {
			Process process = executeCommand(command);
			
			String output = readCommandOutput(process);
			if (output == null || output.length() == 0)
				return Optional.empty();
			return Optional.of(output);
		} catch (IOException e) {
			String errorMsg = String.format("Error looking for port assigned to PID '%s'", pid);
			logger.error(errorMsg, e);
			throw new ImperatorException(Constants.HTTP_CODE_INTERNAL_SERVER_ERROR, errorMsg);
		}
	}
	
	
	private Process executeCommand(String command) throws IOException {
		ProcessBuilder processBuilder = new ProcessBuilder("sh", "-c", command);
		return processBuilder.start();
	}
	
	private String readCommandOutput(Process process) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String output = br.readLine();
		
		logger.info("Command output: {}", output);
		return output;
	}
}
