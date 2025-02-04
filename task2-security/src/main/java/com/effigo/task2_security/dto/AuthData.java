package com.effigo.task2_security.dto;


public class AuthData {
    private String token;


	public AuthData() {
		super();
	}

	public AuthData(String token) {
		super();
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
    
    
}
