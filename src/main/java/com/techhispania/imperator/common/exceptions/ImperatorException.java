package com.techhispania.imperator.common.exceptions;

public class ImperatorException extends RuntimeException {

	private static final long serialVersionUID = -3158554873131712089L;

	private int errorCode;
	
	public ImperatorException(int errorCode, String errorMsg) {
		super(errorMsg);
		this.errorCode = errorCode;
	}

	@Override
	public String toString() {
		return "ImperatorException [errorCode=" + errorCode + ", errorMsg=" + super.getMessage() + "]";
	}
}
