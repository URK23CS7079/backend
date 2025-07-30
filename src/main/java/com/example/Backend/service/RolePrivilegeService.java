package com.example.Backend.service;

import com.example.Backend.entity.Privilege;
import com.example.Backend.entity.Role;
import com.example.Backend.entity.RolePrivilege;
import com.example.Backend.exception.ResourceNotFoundException;
import com.example.Backend.repository.PrivilegeRepository;
import com.example.Backend.repository.RolePrivilegeRepository;
import com.example.Backend.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.Backend.dto.RoleDTO;
import com.example.Backend.dto.request.UpdateRoleRequest;
import com.example.Backend.dto.request.CreateRoleRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RolePrivilegeService {

    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;
    private final RolePrivilegeRepository rolePrivilegeRepository;

    public Map<String, Object> getRolePrivilegeById(Long id) {
        RolePrivilege rp = rolePrivilegeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Role-Privilege association not found with id: " + id));

        Map<String, Object> map = new HashMap<>();
        map.put("role_privilege_id", rp.getRolePrivilegeId());
        map.put("role_id", rp.getRole().getRoleId());
        map.put("privilege_id", rp.getPrivilege().getPrivilegeId());
        map.put("is_active", rp.getIsActive());
        map.put("created_at", rp.getCreatedAt());
        map.put("updated_at", rp.getUpdatedAt());

        return map;
    }

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
                existing.setIsActive(true);
                existing.setUpdatedAt(LocalDateTime.now());
                rolePrivilegeRepository.save(existing);
                return true;
            }
        }

        RolePrivilege newAssociation = RolePrivilege.builder()
                .role(roleOpt.get())
                .privilege(privOpt.get())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        rolePrivilegeRepository.save(newAssociation);
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
    
    public List<Map<String, Object>> getAllRolePrivileges() {
        return rolePrivilegeRepository.findAll().stream()
                .map(rp -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("role_privilege_id", rp.getRolePrivilegeId());
                    map.put("role_id", rp.getRole().getRoleId());
                    map.put("privilege_id", rp.getPrivilege().getPrivilegeId());
                    map.put("is_active", rp.getIsActive());
                    map.put("created_at", rp.getCreatedAt());
                    map.put("updated_at", rp.getUpdatedAt());
                    return map;
                }).toList();
    }
    
    public boolean softDeleteRolePrivilegeById(Long id) {
        Optional<RolePrivilege> rpOpt = rolePrivilegeRepository.findById(id);
        if (rpOpt.isEmpty()) return false;

        RolePrivilege rp = rpOpt.get();
        if (!rp.getIsActive()) return false;

        rp.setIsActive(false);
        rp.setUpdatedAt(LocalDateTime.now());
        rolePrivilegeRepository.save(rp);
        return true;
    }
    public List<Map<String, Object>> getPrivilegesWithAssignmentStatus(Long roleId) {
        List<Privilege> allPrivileges = privilegeRepository.findAllByIsActiveTrue();

        // Get active privilege IDs assigned to this role
        List<Long> assignedPrivilegeIds = rolePrivilegeRepository
                .findByRoleRoleIdAndIsActiveTrue(roleId)
                .stream()
                .map(rp -> rp.getPrivilege().getPrivilegeId())
                .toList();

        // Map each privilege with assigned status
        return allPrivileges.stream().map(privilege -> {
            Map<String, Object> map = new HashMap<>();
            map.put("privilegeId", privilege.getPrivilegeId());
            map.put("privilegeName", privilege.getPrivilegeName());
            map.put("isAssigned", assignedPrivilegeIds.contains(privilege.getPrivilegeId()));
            return map;
        }).toList();
    }

    
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
        		.filter(Role::getIsActive)
                .map(role -> new RoleDTO(role.getRoleId(), role.getRoleName(), role.getNavigateTo()))
                .toList();
    }
    public Map<String, Object> getRoleById(Long id) {
        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        Map<String, Object> map = new HashMap<>();
        map.put("role_id", role.getRoleId());
        map.put("role_name", role.getRoleName());
        map.put("is_active", role.getIsActive());
        map.put("navigate_to", role.getNavigateTo());
        map.put("created_at", role.getCreatedAt());
        map.put("updated_at", role.getUpdatedAt());

        return map;
    }
    public RoleDTO createRoleAndReturn(CreateRoleRequest request) {
        boolean exists = roleRepository.existsByRoleNameIgnoreCase(request.getRoleName());
        if (exists) return null;

        Role role = Role.builder()
                .roleName(request.getRoleName())
                .navigateTo(request.getNavigateTo())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Role saved = roleRepository.save(role);
        return new RoleDTO(saved.getRoleId(), saved.getRoleName(), saved.getNavigateTo());
    }

    public boolean updateRole(UpdateRoleRequest updateRequest) {
        Optional<Role> roleOptional = roleRepository.findById(updateRequest.getRoleId());
        if (roleOptional.isEmpty()) return false;

        Role role = roleOptional.get();
        role.setRoleName(updateRequest.getRoleName());
        role.setNavigateTo(updateRequest.getNavigateTo());
        role.setUpdatedAt(LocalDateTime.now());

        roleRepository.save(role);
        return true;
    }

    public boolean deleteRole(Long roleId) {
        Optional<Role> roleOptional = roleRepository.findById(roleId);
        if (roleOptional.isEmpty()) return false;

        Role role = roleOptional.get();
        role.setIsActive(false); // soft delete
        role.setUpdatedAt(LocalDateTime.now());
        roleRepository.save(role);
        return true;
    }

    public List<Map<String, Object>> getAllPrivileges() {
        return privilegeRepository.findAllByIsActiveTrue().stream()
                .map(privilege -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("privilegeId", privilege.getPrivilegeId());
                    map.put("privilegeName", privilege.getPrivilegeName());
                    return map;
                }).toList();
    }
    public Map<String, Object> getPrivilegeById(Long id) {
        Privilege privilege = privilegeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Privilege not found with id: " + id));

        Map<String, Object> map = new HashMap<>();
        map.put("privilege_id", privilege.getPrivilegeId());
        map.put("privilege_name", privilege.getPrivilegeName());
        map.put("is_active", privilege.getIsActive());
        map.put("created_at", privilege.getCreatedAt());
        map.put("updated_at", privilege.getUpdatedAt());

        return map;
    }
    public boolean createPrivilege(String privilegeName) {
        if (privilegeRepository.existsByPrivilegeNameIgnoreCase(privilegeName)) {
            return false;
        }

        Privilege newPrivilege = Privilege.builder()
                .privilegeName(privilegeName)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        privilegeRepository.save(newPrivilege);
        return true;
    }
    public boolean updatePrivilege(Long id, String newName) {
        Optional<Privilege> optionalPrivilege = privilegeRepository.findById(id);

        if (optionalPrivilege.isPresent()) {
            Privilege privilege = optionalPrivilege.get();
            if (!privilege.getIsActive()) {
                return false;
            }
            // Prevent duplicate names
            if (privilegeRepository.existsByPrivilegeNameIgnoreCase(newName)) {
                return false;
            }
            privilege.setPrivilegeName(newName);
            privilege.setUpdatedAt(LocalDateTime.now());
            privilegeRepository.save(privilege);
            return true;
        }

        return false;
    }
    public boolean deletePrivilege(Long id) {
        Optional<Privilege> optionalPrivilege = privilegeRepository.findById(id);

        if (optionalPrivilege.isPresent()) {
            Privilege privilege = optionalPrivilege.get();

            if (!privilege.getIsActive()) {
                return false; // already deleted
            }
            privilege.setIsActive(false);
            privilege.setUpdatedAt(LocalDateTime.now());
            privilegeRepository.save(privilege);
            return true;
        }

        return false; // not found
    }


}