package com.example.Backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @Column(nullable = false, unique = true)
    private String roleName;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private String navigateTo;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // One role can have many users
    @OneToMany(mappedBy = "role")
    private List<User> users;

    // One role can have many role-privileges
    @OneToMany(mappedBy = "role")
    private List<RolePrivilege> rolePrivileges;
}
