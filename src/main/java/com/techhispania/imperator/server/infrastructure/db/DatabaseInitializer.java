package com.techhispania.imperator.server.infrastructure.db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseInitializer {

    private static final String JDBC_URL = "jdbc:sqlite:imperator.db";

    public static void init() {
    	JPAUtil.createEntityManagerFactory();
    	runSchemaScript();
    }
    
    private static void runSchemaScript() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL)) {

            // Load SQL file from resources
            try (InputStream is = DatabaseInitializer.class.getResourceAsStream("/sql/create_schema.sql")) {
                if (is == null) {
                    throw new RuntimeException("create_schema.sql not found in resources/sql/");
                }

                // Wrap in BufferedReader and automatically close it
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                    String sql = reader.lines().collect(Collectors.joining("\n"));

                    // Split statements by semicolon
                    String[] statements = sql.split(";");

                    try (Statement stmt = conn.createStatement()) {
                        for (String s : statements) {
                            String trimmed = s.trim();
                            if (!trimmed.isEmpty()) {
                                stmt.execute(trimmed);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database schema", e);
        }
    }
}
