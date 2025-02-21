package com.effigo.task2_security.dto;


public class AuthResponse {
	private String status;
	private String message;
	private AuthData data;
//	private String name;
	
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

//	@Override
//	public String toString() {
//		return "AuthResponse [status=" + status + ", message=" + message + ", data=" + data + "]";
//	}

//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	} 
	
	
	
}
