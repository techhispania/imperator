package com.techhispania.imperator.server.domain.model;

public class Application {

	private String name, description, filename;
	
	private ApplicationType type;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public ApplicationType getType() {
		return type;
	}

	public void setType(ApplicationType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Application [name=" + name + ", description=" + description + ", filename=" + filename + ", type="
				+ type + "]";
	}
}
