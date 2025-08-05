package com.example.Backend.repository;

import com.example.Backend.entity.College;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollegeRepository extends JpaRepository<College, Long> {
    List<College> findAllByIsActiveTrue();
}