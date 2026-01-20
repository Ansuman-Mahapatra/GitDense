package com.gitdense.backend.controller;

import com.gitdense.backend.dto.AuthDtos;
import com.gitdense.backend.security.UserPrincipal;
import com.gitdense.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthDtos.AuthResponseDTO> login(@Valid @RequestBody AuthDtos.LoginRequestDTO loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthDtos.AuthResponseDTO> register(@Valid @RequestBody AuthDtos.RegisterRequestDTO registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthDtos.UserDTO> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        AuthDtos.UserDTO userDTO = AuthDtos.UserDTO.builder()
                .id(userPrincipal.getId().toString())
                .name(userPrincipal.getAttributes() != null ? (String) userPrincipal.getAttributes().get("name") : userPrincipal.getUsername())
                .email(userPrincipal.getUsername())
                .role("USER") // Hardcoded for simplified Principal
                .build();
        return ResponseEntity.ok(userDTO);
    }
}
