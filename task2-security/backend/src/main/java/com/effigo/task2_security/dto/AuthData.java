package com.effigo.task2_security.dto;


public class AuthData {
    private String token;
    private String name;


	public AuthData() {
		super();
	}

	public AuthData(String token,String name) {
		super();
		this.token = token;
		this.name=name;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    
	
    
}
