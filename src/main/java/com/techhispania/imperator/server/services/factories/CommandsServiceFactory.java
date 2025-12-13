package com.techhispania.imperator.server.services.factories;

import com.techhispania.imperator.server.services.CommandsService;
import com.techhispania.imperator.server.services.impl.CommandsServiceImpl;

public class CommandsServiceFactory {

	public static CommandsService createCommandsServiceImpl() {
		return new CommandsServiceImpl();
	}
}
