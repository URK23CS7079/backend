package com.example.Backend.controller;

import com.example.Backend.dto.request.CollegeRequestDTO;
import com.example.Backend.dto.response.CollegeResponseDTO;
import com.example.Backend.dto.response.ApiResponse;
import com.example.Backend.service.CollegeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/college")
@PreAuthorize("hasAuthority('manage:college')")
public class CollegeController {

    private final CollegeService collegeService;

    public CollegeController(CollegeService collegeService) {
        this.collegeService = collegeService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> addCollege(@RequestBody CollegeRequestDTO dto) {
        CollegeResponseDTO created = collegeService.addCollege(dto);
        return ResponseEntity.ok(new ApiResponse("success", "College created successfully", created));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllColleges() {
        List<CollegeResponseDTO> colleges = collegeService.getAllColleges();
        return ResponseEntity.ok(new ApiResponse("success", "List of colleges", colleges));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCollegeById(@PathVariable Long id) {
        CollegeResponseDTO college = collegeService.getCollegeById(id);
        return ResponseEntity.ok(new ApiResponse("success", "College details", college));
    }
}
