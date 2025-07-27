package com.example.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RolePrivilegeCheckboxDTO {
    private Long id;
    private String privilegeName;
    private boolean assigned;
}
