package com.example.Backend.controller;

import com.example.Backend.service.LoginService;
import com.example.Backend.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private final OtpService otpService;
    private final LoginService loginService;

    // 1. Send OTP
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        email = email.trim().toLowerCase();
        
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email is required"));
        }
        
        boolean sent = otpService.sendOtp(email);
        if (!sent) {
            return ResponseEntity.status(404).body(Map.of("message", "Invalid email or user not found"));
        }

        return ResponseEntity.ok(Map.of("message", "OTP sent successfully"));
    }

    // 2. Verify OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");

        if (email == null || email.trim().isEmpty() || otp == null || otp.trim().isEmpty()) {
        	return ResponseEntity.badRequest().body(Map.of("message","Email and OTP are required"));
        }
        
        email = email.trim().toLowerCase();
        
        Map<String, Object> response = loginService.verifyAndRespond(email, otp);
        if (response == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid or expired OTP"));
        }

        return ResponseEntity.ok(response);
    }

    //3. Crons response
    @GetMapping("/hi")
    public ResponseEntity<String> sayHi() {
        return ResponseEntity.ok("hi");
    }
}