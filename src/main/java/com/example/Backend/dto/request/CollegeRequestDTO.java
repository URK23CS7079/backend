package com.example.Backend.dto.request;
import lombok.Data;
@Data
public class CollegeRequestDTO {
    // College info
    private String collegeName;
    private String collegeCode;
    private Integer establishedYear;
    private String phoneNumber;
    private String email;
    private String website;
    private String address;
    private String city;
    private String state;

    // Admin info
    private String adminName;
    private String adminEmail;
    private String adminPhoneNumber;
}

