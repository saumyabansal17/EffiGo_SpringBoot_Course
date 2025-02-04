package com.effigo.task2_security.dto;

import java.util.Map;

public class AuthResponse {
	private String status;
	private String message;
	private AuthData data;
	
	public AuthResponse() {
		super();
	}

	public AuthResponse(String status, String message, AuthData data) {
		super();
		this.status = status;
		this.message = message;
		this.data = data;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public AuthData getData() {
		return data;
	}

	public void setData(AuthData data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "AuthResponse [status=" + status + ", message=" + message + ", data=" + data + "]";
	} 
	
	
	
}
