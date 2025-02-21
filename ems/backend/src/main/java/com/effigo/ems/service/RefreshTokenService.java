package com.effigo.ems.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.effigo.ems.model.RefreshToken;
import com.effigo.ems.model.Users;
import com.effigo.ems.repository.RefreshTokenRepository;
import com.effigo.ems.repository.UsersRepository;
import com.effigo.ems.security.JwtUtil;

@Service
public class RefreshTokenService {
    @Value("${app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public RefreshToken createRefreshToken(String emailId) {
    	System.out.println("refresh");
        RefreshToken refreshToken = new RefreshToken();
        Users user = userRepository.findByEmailId(emailId);
        if (user==null) {
            throw new RuntimeException("User not found");
        }
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public Optional<RefreshToken> verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
        	
            refreshTokenRepository.delete(token.getToken());
            return null;
        }
        return Optional.of(token);
    }

    public void deleteByToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(refreshTokenRepository::delete);
    }
    
    public void deleteByUserId(UUID userId) {
    	Users user= userRepository.findById(userId).get();
        if (user==null) {
            throw new RuntimeException("User not found");
        }refreshTokenRepository.deleteByUser(user.getId());
    }
}
