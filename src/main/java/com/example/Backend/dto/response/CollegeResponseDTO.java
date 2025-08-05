package com.example.Backend.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CollegeResponseDTO {
    private Long collegeId;
    private String collegeName;
    private String code;
    private String city;
    private String state;
    private String email;
    private String website;
    private boolean isActive;
    private String adminName;
    private String adminEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}