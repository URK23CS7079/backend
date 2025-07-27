package com.example.Backend.controller;

import com.example.Backend.service.RolePrivilegeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/role-privileges")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('manage:roles')") // ✅ Now based on privilege, not role
public class RolePrivilegeController {

    private final RolePrivilegeService rolePrivilegeService;

    @PostMapping("/assign")
    public ResponseEntity<?> assignPrivilege(@RequestBody Map<String, Long> request) {
        Long roleId = request.get("role_id");
        Long privilegeId = request.get("privilege_id");

        boolean success = rolePrivilegeService.assignPrivilegeToRole(roleId, privilegeId);
        if (success) {
            return ResponseEntity.ok("Privilege assigned successfully.");
        } else {
            return ResponseEntity.badRequest().body("Privilege already exists or role/privilege not found.");
        }
    }

    @PostMapping("/remove")
    public ResponseEntity<?> removePrivilege(@RequestBody Map<String, Long> request) {
        Long roleId = request.get("role_id");
        Long privilegeId = request.get("privilege_id");

        boolean success = rolePrivilegeService.removePrivilegeFromRole(roleId, privilegeId);
        if (success) {
            return ResponseEntity.ok("Privilege removed successfully.");
        } else {
            return ResponseEntity.badRequest().body("Privilege not assigned to role.");
        }
    }
}
