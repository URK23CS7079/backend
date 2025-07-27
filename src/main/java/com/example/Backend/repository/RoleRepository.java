package com.example.Backend.repository;

import com.example.Backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
	boolean existsByRoleNameIgnoreCase(String roleName);
}
