package com.example.Backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "privileges")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Privilege {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long privilegeId;

    @Column(nullable = false)
    private String privilegeName;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // One privilege can be assigned to many roles (via RolePrivilege)
    @OneToMany(mappedBy = "privilege")
    private List<RolePrivilege> rolePrivileges;
}
