package com.example.Backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "otp_verification")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Email to which OTP was sent
    @Column(nullable = false)
    private String email;

    // The OTP itself
    @Column(nullable = false)
    private String otp;

    // Time at which OTP will expire
    private LocalDateTime expiresAt;

    // Flag to indicate whether the OTP has been used
    private boolean verified;

    private LocalDateTime createdAt;
    
    @Builder.Default
    @Column(name = "attempt_count")
    private int attemptCount = 0;

}
