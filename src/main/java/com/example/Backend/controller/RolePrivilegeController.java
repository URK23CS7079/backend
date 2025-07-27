package com.example.Backend.controller;

import com.example.Backend.dto.CreateRoleRequest;
import com.example.Backend.dto.request.ApiResponse;
import com.example.Backend.dto.request.UpdateRoleRequest;
import com.example.Backend.service.RolePrivilegeService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> assignPrivilegeFromRole(@RequestBody Map<String, Long> request) {
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
    public ResponseEntity<?> removePrivilegeFromRole(@RequestBody Map<String, Long> request) {
        Long roleId = request.get("role_id");
        Long privilegeId = request.get("privilege_id");

        boolean success = rolePrivilegeService.removePrivilegeFromRole(roleId, privilegeId);
        if (success) {
            return ResponseEntity.ok("Privilege removed successfully.");
        } else {
            return ResponseEntity.badRequest().body("Privilege not assigned to role.");
        }
    }
    
    @GetMapping("/roles")
    public ResponseEntity<?> getAllRoles() {
        return ResponseEntity.ok(rolePrivilegeService.getAllRoles());
    }

    @PostMapping("/roles")
    public ResponseEntity<ApiResponse> createRole(@RequestBody CreateRoleRequest request) {
        boolean created = rolePrivilegeService.createRole(request);

        if (created) {
            return ResponseEntity.ok(
                    new ApiResponse("success", "Role created successfully")
            );
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ApiResponse("fail", "Role already exists or invalid input")
            );
        }
    }
    
    @PutMapping("/roles")
    public ResponseEntity<ApiResponse> updateRole(@RequestBody UpdateRoleRequest request) {
        boolean updated = rolePrivilegeService.updateRole(request);
        if (updated) {
            return ResponseEntity.ok(
                    new ApiResponse("success", "Role updated successfully")
            );
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ApiResponse("fail", "Invalid role ID or input")
            );
        }
    }
    
    @DeleteMapping("/roles/{roleId}")
    public ResponseEntity<?> deleteRole(@PathVariable Long roleId) {
        boolean deleted = rolePrivilegeService.removeRole(roleId);

        if (deleted) {
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Role deleted successfully"
            ));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "status", "fail",
                "message", "Role not found or already inactive"
            ));
        }
    }

    @GetMapping("/privileges")
    public ResponseEntity<?> getAllPrivileges() {
        return ResponseEntity.ok(rolePrivilegeService.getAllPrivileges());
    }
    @DeleteMapping("/privileges/{id}")
    public ResponseEntity<ApiResponse> deletePrivilege(@PathVariable("id") Long privilegeId) {
        boolean deleted = rolePrivilegeService.deletePrivilege(privilegeId);

        if (deleted) {
            return ResponseEntity.ok(new ApiResponse("success", "Privilege deleted successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("fail", "Privilege not found or already inactive"));
        }
    }

    @PutMapping("/privileges/{id}")
    public ResponseEntity<ApiResponse> updatePrivilege(@PathVariable("id") Long id,
                                                       @RequestBody Map<String, String> request) {
        String newName = request.get("privilege_name");

        boolean updated = rolePrivilegeService.updatePrivilege(id, newName);
        if (updated) {
            return ResponseEntity.ok(new ApiResponse("success", "Privilege updated successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("fail", "Privilege not found, duplicate, or invalid input"));
        }
    }

}
