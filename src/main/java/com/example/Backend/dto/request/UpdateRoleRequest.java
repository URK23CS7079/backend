package com.example.Backend.dto.request;

import lombok.Data;

@Data
public class UpdateRoleRequest {
    private Long roleId;
    private String roleName;
    private String navigateTo;
}
