package com.example.Backend.controller;

import com.example.Backend.dto.RoleDTO;
import com.example.Backend.dto.request.CreateRoleRequest;
import com.example.Backend.dto.request.UpdateRoleRequest;
import com.example.Backend.dto.response.ApiResponse;
import com.example.Backend.service.RolePrivilegeService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/role-privileges")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('manage:roles')") // ✅ Now based on privilege, not role
public class RolePrivilegeController {

    private final RolePrivilegeService rolePrivilegeService;

    @GetMapping("/rp/{id}")
    public ResponseEntity<ApiResponse> getRolePrivilegeById(@PathVariable Long id) {
        return ResponseEntity.ok(
            new ApiResponse("success", "Role privilege retrieved successfully", rolePrivilegeService.getRolePrivilegeById(id))
        );
    }
    @PostMapping("/assign")
    public ResponseEntity<ApiResponse> assignPrivilegeFromRole(@RequestBody Map<String, Long> request) {
        Long roleId = request.get("role_id");
        Long privilegeId = request.get("privilege_id");

        boolean success = rolePrivilegeService.assignPrivilegeToRole(roleId, privilegeId);
        if (success) {
            return ResponseEntity.ok(new ApiResponse(
                "success",
                "Privilege assigned successfully",
                null
            ));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse(
                "error",
                "Privilege already exists or role/privilege not found",
                null
            ));
        }
    }
    @PostMapping("/remove")
    public ResponseEntity<ApiResponse> removePrivilegeFromRole(@RequestBody Map<String, Long> request) {
        Long roleId = request.get("role_id");
        Long privilegeId = request.get("privilege_id");

        boolean success = rolePrivilegeService.removePrivilegeFromRole(roleId, privilegeId);
        if (success) {
            return ResponseEntity.ok(new ApiResponse(
                "success",
                "Privilege removed successfully",
                null
            ));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse(
                "error",
                "Privilege not assigned to role or already removed",
                null
            ));
        }
    }
    @GetMapping("/getall")
    public ResponseEntity<ApiResponse> getAllRolePrivileges() {
        return ResponseEntity.ok(
            new ApiResponse(
                "success",
                "Role privileges retrieved successfully",
                rolePrivilegeService.getAllRolePrivileges()
            )
        );
    }
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<ApiResponse> softDeleteRolePrivilegeById(@PathVariable("id") Long id) {
        boolean success = rolePrivilegeService.softDeleteRolePrivilegeById(id);
        
        if (success) {
            return ResponseEntity.ok(new ApiResponse("success", "Role-privilege association removed successfully"));
        } else {
            return ResponseEntity
                .badRequest()
                .body(new ApiResponse("error", "Role-privilege association not found or already removed"));
        }
    }
    @GetMapping("/privileges/with-assigned-status/{roleId}")
    public ResponseEntity<ApiResponse> getPrivilegesWithAssignmentStatus(@PathVariable Long roleId) {
        return ResponseEntity.ok(
            new ApiResponse(
                "success",
                "Privileges fetched with assignment status",
                rolePrivilegeService.getPrivilegesWithAssignmentStatus(roleId)
            )
        );
    }

    
    @GetMapping("/roles")
    public ResponseEntity<ApiResponse> getAllRoles() {
        List<RoleDTO> roles = rolePrivilegeService.getAllRoles();
        return ResponseEntity.ok(new ApiResponse("success", "Roles fetched successfully", roles));
    }
    
    @GetMapping("/roles/{id}")
    public ResponseEntity<ApiResponse> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(
            new ApiResponse("success", "Role retrieved successfully", rolePrivilegeService.getRoleById(id))
        );
    }
    
    @PostMapping("/roles")
    public ResponseEntity<ApiResponse> createRole(@RequestBody CreateRoleRequest request) {
        RoleDTO role = rolePrivilegeService.createRoleAndReturn(request);
        if (role != null) {
            return ResponseEntity.ok(new ApiResponse("success", "Role created successfully", role));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("fail", "Role already exists or invalid input"));
        }
    }

    @PutMapping("/roles/{roleId}")
    public ResponseEntity<ApiResponse> updateRole(@PathVariable Long roleId, @RequestBody UpdateRoleRequest request) {
        request.setRoleId(roleId);
        boolean updated = rolePrivilegeService.updateRole(request);
        if (updated) {
            return ResponseEntity.ok(new ApiResponse("success", "Role updated successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("fail", "Role not found"));
        }
    }

    @DeleteMapping("/roles/{roleId}")
    public ResponseEntity<ApiResponse> deleteRole(@PathVariable Long roleId) {
        boolean deleted = rolePrivilegeService.deleteRole(roleId);
        if (deleted) {
            return ResponseEntity.ok(new ApiResponse("success", "Role deleted successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("fail", "Role not found"));
        }
    }
    
    @GetMapping("/privileges")
    public ResponseEntity<ApiResponse> getAllPrivileges() {
        return ResponseEntity.ok(
            new ApiResponse(
                "success",
                "Privileges fetched successfully",
                rolePrivilegeService.getAllPrivileges()
            )
        );
    }
    @GetMapping("/privileges/{id}")
    public ResponseEntity<ApiResponse> getPrivilegeById(@PathVariable Long id) {
        return ResponseEntity.ok(
            new ApiResponse("success", "Privilege retrieved successfully", rolePrivilegeService.getPrivilegeById(id))
        );
    }
    @PostMapping("/privileges")
    public ResponseEntity<ApiResponse> createPrivilege(@RequestBody Map<String, String> request) {
        String privilegeName = request.get("privilegeName");

        boolean created = rolePrivilegeService.createPrivilege(privilegeName);
        if (created) {
            return ResponseEntity.ok(
                new ApiResponse("success", "Privilege created successfully", null)
            );
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse("error", "Privilege already exists", null));
        }
    }
    @PutMapping("/privileges/{id}")
    public ResponseEntity<ApiResponse> updatePrivilege(
            @PathVariable Long id,
            @RequestBody Map<String, String> requestBody) {

        String newName = requestBody.get("privilegeName");

        boolean updated = rolePrivilegeService.updatePrivilege(id, newName);
        if (updated) {
            return ResponseEntity.ok(new ApiResponse(
                "success",
                "Privilege updated successfully",
                null
            ));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(
                "error",
                "Privilege not found or is inactive",
                null
            ));
        }
    }
    @DeleteMapping("/privileges/{id}")
    public ResponseEntity<ApiResponse> deletePrivilege(@PathVariable Long id) {
        boolean deleted = rolePrivilegeService.deletePrivilege(id);

        if (deleted) {
            return ResponseEntity.ok(new ApiResponse(
                "success",
                "Privilege deleted successfully",
                null
            ));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(
                "error",
                "Privilege not found or already deleted",
                null
            ));
        }
    }

}