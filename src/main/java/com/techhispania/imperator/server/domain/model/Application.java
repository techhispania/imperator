package com.techhispania.imperator.server.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "applications")
public class Application {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String name, description, filename, pid;
	
	private ApplicationType type;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
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

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public ApplicationType getType() {
		return type;
	}

	public void setType(ApplicationType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Application [id=" + id + ", name=" + name + ", description=" + description + ", filename=" + filename
				+ ", pid=" + pid + ", type=" + type + "]";
	}
}
