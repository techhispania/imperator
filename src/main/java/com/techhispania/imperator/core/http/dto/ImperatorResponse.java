package com.techhispania.imperator.core.http.dto;

public class ImperatorResponse<T> {

	private int responseCode;
	
	private T body;

	public ImperatorResponse(int responseCode, T body) {
		this.responseCode = responseCode;
		this.body = body;
	}
	
	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public T getBody() {
		return body;
	}

	public void setBody(T body) {
		this.body = body;
	}

	@Override
	public String toString() {
		return "ImperatorResponse [responseCode=" + responseCode + ", body=" + body + "]";
	}
}
