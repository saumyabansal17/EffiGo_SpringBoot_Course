package com.effigo.ems.model;

import com.effigo.ems.enums.RoleStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Role {
    
    @Id 
    private int role_id;

    @Enumerated(EnumType.STRING)
    private RoleStatus status;
    
    private String role;


}
