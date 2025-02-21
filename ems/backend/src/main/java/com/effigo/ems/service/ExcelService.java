package com.effigo.ems.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.effigo.ems.dto.ImportResult;
import com.effigo.ems.enums.UserStatus;
import com.effigo.ems.model.Users;
import com.effigo.ems.repository.RoleRepository;
import com.effigo.ems.repository.UsersRepository;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ExcelService {

    @Autowired
    private UsersRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Users> importUsers(MultipartFile file) {
        List<Users> userList = new ArrayList<>();
        List<Users> savedUsers = new ArrayList<>();
        List<Users> skippedUsers = new ArrayList<>();
        DataFormatter dataFormatter = new DataFormatter(); 

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            log.info("Processing Excel file: {}", file.getOriginalFilename());

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; 

                Users user = new Users();
                user.setName(getCellValue(row.getCell(0)));  // Name
                user.setEmailId(getCellValue(row.getCell(1))); // Email
                user.setPhone_no(getCellValue(row.getCell(2))); // Phone (Handles numbers)

                String rawPassword = getCellValue(row.getCell(3));
                String firstHash = passwordEncoder.encode(rawPassword);
                log.info("First hash generated for {}: {}", user.getEmailId(), firstHash);

                user.setPassword(passwordEncoder.encode(firstHash));
                log.info("Second hash stored for {}: {}", user.getEmailId(), user.getPassword());

                String roleName = getCellValue(row.getCell(4));
                user.setRole(roleRepository.findByRole(roleName));
                log.info("Assigned role: {} to user: {}", roleName, user.getEmailId());

                user.setStatus(UserStatus.ACTIVE);
                user.setRegister_at();

                if (userRepository.existsByEmailId(user.getEmailId())) {
                    log.info("Skipping user with existing email: {}", user.getEmailId());
                    skippedUsers.add(user);
                } else {
                    savedUsers.add(user);
                }
            }

            log.info("Saving {} new users to the database...", savedUsers.size());
            userRepository.saveAll(savedUsers);

            return skippedUsers;
        } catch (Exception e) {
            log.error("Error processing Excel file: {}", e.getMessage(), e);
            throw new RuntimeException("Error processing Excel file: " + e.getMessage());
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return ""; 
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell); 
    }
}
