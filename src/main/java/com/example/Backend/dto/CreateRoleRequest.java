package com.example.Backend.dto;

import lombok.Data;

@Data
public class CreateRoleRequest {
    private String roleName;
    private String navigateTo;
}
