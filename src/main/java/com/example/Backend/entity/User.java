package com.example.Backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users") // Avoid using reserved word "user"
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String phoneNumber;

    @Builder.Default
    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private boolean isActive = true;
    
    // Foreign key to Role table
    @ManyToOne(optional = false)
    @JoinColumn(name = "role_id")
    private Role role;
    
    
}
