package com.example.Backend.service;

import com.example.Backend.entity.Privilege;
import com.example.Backend.entity.Role;
import com.example.Backend.entity.RolePrivilege;
import com.example.Backend.repository.PrivilegeRepository;
import com.example.Backend.repository.RolePrivilegeRepository;
import com.example.Backend.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SuperAdminService {

    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;
    private final RolePrivilegeRepository rolePrivilegeRepository;

    public boolean assignPrivilegeToRole(Long roleId, Long privilegeId) {
        Optional<Role> roleOpt = roleRepository.findById(roleId);
        Optional<Privilege> privOpt = privilegeRepository.findById(privilegeId);

        if (roleOpt.isEmpty() || privOpt.isEmpty()) return false;

        boolean exists = rolePrivilegeRepository.existsByRoleRoleIdAndPrivilegePrivilegeId(roleId, privilegeId);
        if (exists) return false;

        RolePrivilege rp = RolePrivilege.builder()
                .role(roleOpt.get())
                .privilege(privOpt.get())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        rolePrivilegeRepository.save(rp);
        return true;
    }

    public boolean removePrivilegeFromRole(Long roleId, Long privilegeId) {
        Optional<RolePrivilege> rpOpt = rolePrivilegeRepository.findByRoleRoleIdAndPrivilegePrivilegeId(roleId, privilegeId);
        if (rpOpt.isEmpty()) return false;

        RolePrivilege rp = rpOpt.get();
        if (!rp.getIsActive()) return false;

        rp.setIsActive(false);
        rp.setUpdatedAt(LocalDateTime.now());
        rolePrivilegeRepository.save(rp);
        return true;
    }
}
