package com.techhispania.imperator.server.infrastructure.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techhispania.imperator.common.exceptions.ImperatorException;
import com.techhispania.imperator.common.utils.Constants;

public class DatabaseConnector {

	private static final Logger logger = LogManager.getLogger(DatabaseConnector.class);

	// TODO retrieve this from configuration file
	private static final String CONNECTION_URI = "jdbc:sqlite:imperator.db";
	
	private static Connection connection;
	
	public Connection openConnection() throws ImperatorException {
		try {
			connection = DriverManager.getConnection(CONNECTION_URI);
		} catch (SQLException e) {
			logger.error("Error opening database connection.", e);
			throw new ImperatorException(Constants.HTTP_CODE_INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return connection;
	}
	
	public void closeConnection() throws ImperatorException {
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException e) {
			logger.error("Error closing database connection.", e);
			throw new ImperatorException(Constants.HTTP_CODE_INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
}
