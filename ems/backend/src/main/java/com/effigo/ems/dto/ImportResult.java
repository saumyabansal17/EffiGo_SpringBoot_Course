package com.effigo.ems.dto;

import java.util.List;
import com.effigo.ems.model.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImportResult {
    private String message;
    private List<Users> skippedUsers;


    // getters and setters
}
