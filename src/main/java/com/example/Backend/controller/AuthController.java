package com.example.Backend.controller;

import com.example.Backend.service.JwtService;
import com.example.Backend.repository.UserRepository;
import com.example.Backend.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    // 1️⃣ Refresh token endpoint
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshAccessToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refresh_token");
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Refresh token is required"));
        }

        try {
            String email = jwtService.extractSubject(refreshToken);
            if (!jwtService.isTokenValid(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid refresh token"));
            }

            Map<String, Object> claims = new HashMap<>();
            claims.put("email", email);

            String newAccessToken = jwtService.generateAccessToken(email, claims);
            return ResponseEntity.ok(Map.of("access_token", newAccessToken));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid refresh token"));
        }
    }

    // 2️⃣ User info endpoint
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getUserDetails(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Missing or invalid token"));
        }

        String token = authHeader.substring(7);
        if (!jwtService.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid token"));
        }

        String email = jwtService.extractSubject(token);
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not found"));
        }

        User user = userOpt.get();
        Map<String, Object> response = new HashMap<>();
        response.put("user_id", user.getUserId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("phone_number", user.getPhoneNumber());

        return ResponseEntity.ok(response);
    }
}
