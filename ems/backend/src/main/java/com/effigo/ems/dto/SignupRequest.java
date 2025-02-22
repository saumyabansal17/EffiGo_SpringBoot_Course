package com.effigo.ems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SignupRequest {
	private String name;
    private String password;
    private String emailId;
    private String phone_no;
    private int role_id;
  
    
}
