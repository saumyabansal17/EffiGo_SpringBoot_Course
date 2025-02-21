package com.effigo.ems.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.effigo.ems.model.Users;
import com.effigo.ems.repository.UsersRepository;
import com.effigo.ems.enums.UserStatus;

import java.util.List;

@Service
public class ScheduleTaskService {

    @Autowired
    private UsersRepository adminRepository;

    @Autowired
    private EmailService emailService;

     @Scheduled(cron = "0 48 12 * * *") 
    public void autoAcceptPendingRequests() {
        List<Users> pendingUsers = adminRepository.findByStatus(UserStatus.PENDING);

        for (Users user : pendingUsers) {
            if (user.getStatus() == UserStatus.PENDING) {
                user.setStatus(UserStatus.ACTIVE); 
                adminRepository.save(user);

                emailService.sendAutoAcceptanceNotification(user.getEmailId());
            }
        }
    }
}
