package com.example.Backend.repository;

import com.example.Backend.entity.Privilege;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
	boolean existsByPrivilegeNameIgnoreCase(String privilegeName);
	List<Privilege> findAllByIsActiveTrue();
	
}