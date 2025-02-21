package com.effigo.ems.dto;

import java.util.UUID;

import com.effigo.ems.enums.UserStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class UserDetailsDto {
	private String emailId;
	private String phone;
	private UUID id;
	private String role;
	
	private String status; 

    public UserStatus getStatusAsEnum() {
        return UserStatus.valueOf(status); 
    }
}
