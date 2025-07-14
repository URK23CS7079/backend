package com.example.Backend.repository;

import com.example.Backend.entity.RolePrivilege;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RolePrivilegeRepository extends JpaRepository<RolePrivilege, Long> {

    List<RolePrivilege> findByRoleRoleIdAndIsActiveTrue(Long roleId);

    // Check if privilege is already assigned to role
    boolean existsByRoleRoleIdAndPrivilegePrivilegeId(Long roleId, Long privilegeId);

    // Get the RolePrivilege row (for deactivation/removal)
    Optional<RolePrivilege> findByRoleRoleIdAndPrivilegePrivilegeId(Long roleId, Long privilegeId);
}
