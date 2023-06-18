package com.example.demo.src.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class CacheService {
    private final JdbcTemplate jdbcTemplate;
    private static final int EXPIRATION_MINUTES = 5;

    public CacheService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveVerificationCode(String phoneNum, String verificationCode) {
        String query = "INSERT INTO verification_code (phone_num, code, created_at) VALUES (?, ?, ?)";
        LocalDateTime expirationTime = LocalDateTime.now().plus(EXPIRATION_MINUTES, ChronoUnit.MINUTES);
        jdbcTemplate.update(query, phoneNum, verificationCode, expirationTime);
    }

    public String getVerificationCode(String phoneNum) {
        String query = "SELECT code FROM verification_code WHERE phone_num = ? AND create_at > NOW()";
        List<String> codes = jdbcTemplate.query(query, new Object[]{phoneNum}, (rs, rowNum) -> rs.getString("code"));
        if (codes.isEmpty()) {
            return null; // 또는 원하는 처리를 수행
        }
        return codes.get(0);
    }

    public void deleteVerificationCode(String phoneNum) {
        String query = "DELETE FROM verification_code WHERE phone_num = ?";
        jdbcTemplate.update(query, phoneNum);
    }
}
