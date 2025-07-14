package com.example.Backend.controller;

import com.example.Backend.service.SuperAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/superadmin")
@RequiredArgsConstructor
public class SuperAdminController {

    private final SuperAdminService superAdminService;

    @PostMapping("/assign-privilege")
    public ResponseEntity<?> assignPrivilege(@RequestBody Map<String, Long> request) {
        Long roleId = request.get("role_id");
        Long privilegeId = request.get("privilege_id");

        boolean success = superAdminService.assignPrivilegeToRole(roleId, privilegeId);
        if (success) {
            return ResponseEntity.ok("Privilege assigned successfully.");
        } else {
            return ResponseEntity.badRequest().body("Privilege already exists or role/privilege not found.");
        }
    }

    @PostMapping("/remove-privilege")
    public ResponseEntity<?> removePrivilege(@RequestBody Map<String, Long> request) {
        Long roleId = request.get("role_id");
        Long privilegeId = request.get("privilege_id");

        boolean success = superAdminService.removePrivilegeFromRole(roleId, privilegeId);
        if (success) {
            return ResponseEntity.ok("Privilege removed successfully.");
        } else {
            return ResponseEntity.badRequest().body("Privilege not assigned to role.");
        }
    }
}
