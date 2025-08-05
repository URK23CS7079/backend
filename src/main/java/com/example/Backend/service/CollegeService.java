package com.example.Backend.service;

import com.example.Backend.dto.request.CollegeRequestDTO;
import com.example.Backend.dto.response.CollegeResponseDTO;
import com.example.Backend.entity.College;
import com.example.Backend.entity.Role;
import com.example.Backend.entity.User;
import com.example.Backend.repository.CollegeRepository;
import com.example.Backend.repository.RoleRepository;
import com.example.Backend.repository.UserRepository;
import com.example.Backend.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CollegeService {

    private final CollegeRepository collegeRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public CollegeService(CollegeRepository collegeRepository,
                          UserRepository userRepository,
                          RoleRepository roleRepository) {
        this.collegeRepository = collegeRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public CollegeResponseDTO addCollege(CollegeRequestDTO dto) {
        // Step 1: Create Admin User
        Role adminRole = roleRepository.findById(4L)
                .orElseThrow(() -> new ResourceNotFoundException("Admin role not found"));

        User admin = User.builder()
                .name(dto.getAdminName())
                .email(dto.getAdminEmail())
                .phoneNumber(dto.getAdminPhoneNumber())
                .role(adminRole)
                .isActive(true)
                .build();

        admin = userRepository.save(admin);

        // Step 2: Create College
        College college = College.builder()
                .collegeName(dto.getCollegeName())
                .code(dto.getCollegeCode())
                .establishedYear(dto.getEstablishedYear())
                .phoneNumber(dto.getPhoneNumber())
                .email(dto.getEmail())
                .website(dto.getWebsite())
                .address(dto.getAddress())
                .city(dto.getCity())
                .state(dto.getState())
                .country("India") // Default for now
                .isActive(true)
                .admin(admin)
                .build();

        College saved = collegeRepository.save(college);

        return mapToResponse(saved);
    }

    public List<CollegeResponseDTO> getAllColleges() {
        return collegeRepository.findAllByIsActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CollegeResponseDTO getCollegeById(Long id) {
        College college = collegeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("College not found"));
        return mapToResponse(college);
    }

    private CollegeResponseDTO mapToResponse(College college) {
        CollegeResponseDTO dto = new CollegeResponseDTO();
        dto.setCollegeId(college.getCollegeId());
        dto.setCollegeName(college.getCollegeName());
        dto.setCode(college.getCode());
        dto.setCity(college.getCity());
        dto.setState(college.getState());
        dto.setEmail(college.getEmail());
        dto.setWebsite(college.getWebsite());
        dto.setActive(college.isActive());
        dto.setAdminName(college.getAdmin().getName());
        dto.setAdminEmail(college.getAdmin().getEmail());
        dto.setCreatedAt(college.getCreatedAt());
        dto.setUpdatedAt(college.getUpdatedAt());
        return dto;
    }
}