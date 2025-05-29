package com.example.Backend.repository;

import com.example.Backend.entity.RolePrivilege;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RolePrivilegeRepository extends JpaRepository<RolePrivilege, Long> {
    List<RolePrivilege> findByRoleRoleIdAndIsActiveTrue(Long roleId);
}
