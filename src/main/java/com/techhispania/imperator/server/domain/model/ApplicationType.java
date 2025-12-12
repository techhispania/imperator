package com.techhispania.imperator.server.domain.model;

public enum ApplicationType {

	JAR("jar"),
	SPRING_BOOT("spring-boot"),
	QUARKUS("quarkus"),
	MICRONAUT("micronaut");
	
	private String value;
	
	private ApplicationType(String value) {
		this.value = value;
	}
	
	public String value() {
		return this.value;
	}
	
	public static ApplicationType fromValue(String value) throws IllegalArgumentException {
		for (ApplicationType t : ApplicationType.values()) {
			if (value.equals(t.value())) {
				return t;
			}
		}
		throw new IllegalArgumentException("Unknown application type: " + value);
	}
}
