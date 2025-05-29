package com.example.Backend.service;

import com.example.Backend.entity.OtpVerification;
import com.example.Backend.entity.User;
import com.example.Backend.repository.OtpVerificationRepository;
import com.example.Backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.security.SecureRandom;


@Service
@RequiredArgsConstructor
public class OtpService {

    private final UserRepository userRepository;
    private final OtpVerificationRepository otpVerificationRepository;
    private final JavaMailSender mailSender;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${spring.mail.username}") // From application.properties
    private String fromEmail;

    // Generate and send OTP
    @Transactional
    public boolean sendOtp(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return false;
        
        User user = userOpt.get();
        if (!user.isActive() || !user.getRole().getIsActive()) {
            // Either user or role is inactive
            return false;
        }

        // Check if a valid, unexpired OTP already exists
        Optional<OtpVerification> existingOtp = otpVerificationRepository.findTopByEmailOrderByCreatedAtDesc(email);
        if (existingOtp.isPresent()) {
            OtpVerification latest = existingOtp.get();
            if (!latest.isVerified() && latest.getExpiresAt().isAfter(LocalDateTime.now())) {
                // Valid OTP already sent
                return true; // Or return false depending on behavior you want
            }
        }

        String otp = generateOtp();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(5);

        OtpVerification otpVerification = OtpVerification.builder()
                .email(email)
                .otp(otp)
                .expiresAt(expiresAt)
                .createdAt(now)
                .verified(false)
                .build();

        otpVerificationRepository.save(otpVerification);
        sendEmail(email, otp);
        return true;
    }


    // Helper: Generate 6-digit OTP
    private String generateOtp() {
        int otp = secureRandom.nextInt(1_000_000); // 0 to 999999
        return String.format("%06d", otp);
    }

    // Helper: Send email
    private void sendEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Your Login OTP");
        message.setText("Your OTP is: " + otp + "\n\nIt will expire in 5 minutes.");
        mailSender.send(message);
    }
}
