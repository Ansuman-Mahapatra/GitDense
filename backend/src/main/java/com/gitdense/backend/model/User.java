package com.gitdense.backend.model;

import com.gitdense.backend.model.enums.AuthProvider;
import com.gitdense.backend.model.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntity {

    @Email
    @NotNull
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    private String password;

    @Enumerated(EnumType.STRING)
    @NotNull
    private AuthProvider provider;

    private String providerId;

    private String profilePicture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    // Default constructor
    public User() {
        this.role = Role.USER;
        this.provider = AuthProvider.LOCAL;
    }
}
