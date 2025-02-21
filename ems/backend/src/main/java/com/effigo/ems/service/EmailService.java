package com.effigo.ems.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.effigo.ems.model.Users;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendApprovalRequest(String superAdminEmail, Users user) {
        try {
            log.info("Sending email to: {}", superAdminEmail);  
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(superAdminEmail);
            helper.setSubject("New User Approval Request");

            String approvalLink = "http://localhost:8080/admin/approveUser?email=" + user.getEmailId();
            String rejectionLink = "http://localhost:8080/admin/rejectUser?email=" + user.getEmailId();

            String content = "<p>A new user has registered: </p>"
                    + "<p>Email: " + user.getEmailId() + "</p>"
                    + "<p>Phone: " + user.getPhone_no() + "</p>"
                    + "<p>Click below to approve or reject:</p>"
                    + "<a href='" + approvalLink + "'>Approve</a> | "
                    + "<a href='" + rejectionLink + "'>Reject</a>";

            helper.setText(content, true);

            mailSender.send(message);
            log.info("Email sent successfully!");
        } catch (Exception e) {
            log.error("Failed to send email", e); 
        }
    }
    
    public void sendAutoAcceptanceNotification(String userEmail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(userEmail);
            message.setSubject("Request Auto-Accepted");
            message.setText("Dear User,\n\nYour registration request has been automatically accepted after the specified time frame.\n\nBest Regards,\nThe Team");

            mailSender.send(message);
            log.info("Auto-acceptance notification sent to {}", userEmail);
        } catch (Exception e) {
            log.error("Failed to send auto-acceptance notification", e);
        }
    }
    
    public void sendAcceptanceNotification(String userEmail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(userEmail);
            message.setSubject("Request Accepted");
            message.setText("Dear User,\n\nYour registration request has been accepted.\n\nBest Regards,\nThe Team");

            mailSender.send(message);
            log.info("Acceptance notification sent to {}", userEmail);
        } catch (Exception e) {
            log.error("Failed to send acceptance notification", e);
        }
    }
}
