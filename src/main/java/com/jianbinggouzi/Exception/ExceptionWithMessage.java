package com.jianbinggouzi.Exception;

public class ExceptionWithMessage extends Exception {

	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ExceptionWithMessage(String message) {
		this.message = message;
	}

}
