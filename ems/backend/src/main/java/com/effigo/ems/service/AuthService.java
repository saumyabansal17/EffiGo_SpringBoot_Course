package com.effigo.ems.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.effigo.ems.dto.AuthResponse;
import com.effigo.ems.dto.LoginRequest;
import com.effigo.ems.dto.SignupRequest;
import com.effigo.ems.enums.UserStatus;
import com.effigo.ems.model.RefreshToken;
import com.effigo.ems.model.Role;
import com.effigo.ems.model.Users;
import com.effigo.ems.repository.RefreshTokenRepository;
import com.effigo.ems.repository.RoleRepository;
import com.effigo.ems.repository.UsersRepository;
import com.effigo.ems.security.JwtUtil;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthService {
    @Autowired
    private UsersRepository adminRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    @Autowired
    private EmailService emailService;

    @Autowired
    private LoggingHistoryService logingHistoryService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public AuthService() {
        super();
    }

    public String register(SignupRequest request) {
        try {
            log.info("Registering user with email: {}", request.getEmailId());
            
            if (adminRepository.findByEmailId(request.getEmailId()) != null) {
                log.warn("Email {} is already in use!", request.getEmailId());
                return "Email is already in use!";
            }

            Users user = new Users();
            user.setEmailId(request.getEmailId());
            user.setName(request.getName());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setPhone_no(request.getPhone_no());
            user.setRegister_at();

            Role userRole = roleRepository.findById(request.getRole_id())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid role ID!"));

            String superAdminEmail = adminRepository.findSuperAdminEmail();
            emailService.sendApprovalRequest(superAdminEmail, user);
            
            user.setStatus(UserStatus.PENDING);
            user.setRole(userRole);
            adminRepository.save(user);

            log.info("User registered successfully: {}", request.getEmailId());
            return "User registered successfully!";
        } catch (Exception e) {
            log.error("Error registering user: {}", e.getMessage(), e);
            return "Error: " + e.getMessage();
        }
    }

    public AuthResponse login(LoginRequest request, HttpServletResponse response) throws Exception {
        try {
            log.info("Attempting login for user: {}", request.getEmailId());
            Users user = adminRepository.findByEmailId(request.getEmailId());
            
            if (user == null) {
                log.warn("User not found: {}", request.getEmailId());
                throw new Exception("User not found");
            }

            int r_id = adminRepository.findIdByEmail(request.getEmailId());
            String status = roleRepository.findStatusById(r_id);

            if (!status.equals("ACTIVE") || user.getStatus() != UserStatus.ACTIVE) {
                log.warn("User {} is inactive", request.getEmailId());
//                throw new Exception("User is inactive, please contact admin.");
                return new AuthResponse("Failed", "User is inactive, please contact admin.");
            }

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                log.warn("Invalid password for user: {}", request.getEmailId());
                throw new Exception("Invalid email or password");
            }

            String token = jwtUtil.generateTokenFromUsername(request.getEmailId());
            log.info("Generated token for user: {}", request.getEmailId());

            ResponseCookie cookie = ResponseCookie.from("token", token)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(1*60*60)
                    .sameSite("Lax")
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            log.info("Token set in cookie for user: {}", request.getEmailId());

            refreshTokenRepository.deleteByUser(user.getId());
            RefreshToken refreshToken = refreshTokenRepository.findByUser(user);
            
            if (refreshToken == null || refreshTokenService.verifyExpiration(refreshToken) == null) {
                refreshToken = refreshTokenService.createRefreshToken(user.getEmailId());
            }

            logingHistoryService.saveLoginHistory(user);
            log.info("Login successful for user: {}", request.getEmailId());
            return new AuthResponse("Success", "Login successful");
        } catch (Exception e) {
            log.error("Login failed for user {}: {}", request.getEmailId(), e.getMessage(), e);
            throw new Exception("Login failed", e);
        }
    }

    public void logout(HttpServletResponse response) throws Exception {
        try {
            log.info("Logging out user");
            ResponseCookie cookie = ResponseCookie.from("token", "")
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(0)
                    .sameSite("Lax")
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            log.info("User logged out successfully");
        } catch (Exception e) {
            log.error("Logout failed: {}", e.getMessage(), e);
            throw new Exception("Logout failed", e);
        }
    }
}
