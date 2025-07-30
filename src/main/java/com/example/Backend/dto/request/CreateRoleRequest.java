package com.example.Backend.dto.request;

import lombok.Data;

@Data
public class CreateRoleRequest {
    private String roleName;
    private String navigateTo;
}