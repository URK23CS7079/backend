package com.example.Backend.service;

import com.example.Backend.entity.Privilege;
import com.example.Backend.entity.Role;
import com.example.Backend.entity.RolePrivilege;
import com.example.Backend.repository.PrivilegeRepository;
import com.example.Backend.repository.RolePrivilegeRepository;
import com.example.Backend.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.Backend.dto.RoleDTO;
import com.example.Backend.dto.request.UpdateRoleRequest;
import com.example.Backend.dto.CreateRoleRequest;


import java.time.LocalDateTime;
import java.util.Optional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class RolePrivilegeService {

    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;
    private final RolePrivilegeRepository rolePrivilegeRepository;

    public boolean assignPrivilegeToRole(Long roleId, Long privilegeId) {
        Optional<Role> roleOpt = roleRepository.findById(roleId);
        Optional<Privilege> privOpt = privilegeRepository.findById(privilegeId);

        if (roleOpt.isEmpty() || privOpt.isEmpty()) return false;

        Optional<RolePrivilege> existingOpt =
                rolePrivilegeRepository.findByRoleRoleIdAndPrivilegePrivilegeId(roleId, privilegeId);

        if (existingOpt.isPresent()) {
            RolePrivilege existing = existingOpt.get();

            if (existing.getIsActive()) {
                return false; // Already active
            } else {
                // Reactivate if previously removed
                existing.setIsActive(true);
                existing.setUpdatedAt(LocalDateTime.now());
                rolePrivilegeRepository.save(existing);
                return true;
            }
        }

        // If not exists at all, create new
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
    
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .filter(Role::getIsActive)
                .map(role -> new RoleDTO(
                        role.getRoleId(),
                        role.getRoleName(),
                        role.getNavigateTo()
                ))
                .collect(Collectors.toList());
    }
        
    public boolean createRole(CreateRoleRequest request) {
        if (request.getRoleName() == null || request.getNavigateTo() == null ||
            request.getRoleName().isBlank() || request.getNavigateTo().isBlank()) {
            return false;
        }

        boolean exists = roleRepository.existsByRoleNameIgnoreCase(request.getRoleName());
        if (exists) return false;

        Role role = Role.builder()
                .roleName(request.getRoleName())
                .navigateTo(request.getNavigateTo())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        roleRepository.save(role);
        return true;
    }
    public boolean updateRole(UpdateRoleRequest request) {
        if (request.getRoleId() == null || 
            request.getRoleName() == null || 
            request.getNavigateTo() == null || 
            request.getRoleName().isBlank() || 
            request.getNavigateTo().isBlank()) {
            return false;
        }

        Optional<Role> opt = roleRepository.findById(request.getRoleId());
        if (opt.isEmpty()) return false;

        Role role = opt.get();
        role.setRoleName(request.getRoleName());
        role.setNavigateTo(request.getNavigateTo());
        role.setUpdatedAt(LocalDateTime.now());

        roleRepository.save(role);
        return true;
    }
    public boolean removeRole(Long roleId) {
        Optional<Role> opt = roleRepository.findById(roleId);
        if (opt.isEmpty()) return false;

        Role role = opt.get();
        if (!role.getIsActive()) return false;

        role.setIsActive(false);
        role.setUpdatedAt(LocalDateTime.now());
        roleRepository.save(role);
        return true;
    }
    public List<Map<String, Object>> getAllPrivileges() {
        List<Privilege> privileges = privilegeRepository.findAllByIsActiveTrue();

        return privileges.stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("privilege_id", p.getPrivilegeId());
            map.put("privilege_name", p.getPrivilegeName());
            return map;
        }).collect(Collectors.toList());
    }
    public boolean deletePrivilege(Long privilegeId) {
        Optional<Privilege> optionalPrivilege = privilegeRepository.findById(privilegeId);
        if (optionalPrivilege.isEmpty()) return false;

        Privilege privilege = optionalPrivilege.get();
        if (!privilege.getIsActive()) return false;

        privilege.setIsActive(false);
        privilege.setUpdatedAt(LocalDateTime.now());
        privilegeRepository.save(privilege);
        return true;
    }
    public boolean updatePrivilege(Long id, String newPrivilegeName) {
        if (newPrivilegeName == null || newPrivilegeName.isBlank()) return false;

        Optional<Privilege> optionalPrivilege = privilegeRepository.findById(id);
        if (optionalPrivilege.isEmpty()) return false;

        Privilege privilege = optionalPrivilege.get();

        // If name is unchanged, do nothing
        if (privilege.getPrivilegeName().equalsIgnoreCase(newPrivilegeName)) return false;

        // Check for duplicate name
        boolean exists = privilegeRepository.existsByPrivilegeNameIgnoreCase(newPrivilegeName);
        if (exists) return false;

        privilege.setPrivilegeName(newPrivilegeName);
        privilege.setUpdatedAt(LocalDateTime.now());

        privilegeRepository.save(privilege);
        return true;
    }


}