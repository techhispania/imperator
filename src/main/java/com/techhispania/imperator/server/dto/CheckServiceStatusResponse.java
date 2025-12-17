package com.techhispania.imperator.server.dto;

public record CheckServiceStatusResponse(String serviceName, String status, String port, String pid) {

}
