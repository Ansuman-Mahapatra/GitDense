package com.gitdense.backend.service;

import com.gitdense.backend.dto.AuthDtos;
import com.gitdense.backend.exception.BadRequestException;
import com.gitdense.backend.model.User;
import com.gitdense.backend.model.enums.AuthProvider;
import com.gitdense.backend.repository.UserRepository;
import com.gitdense.backend.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    public AuthDtos.AuthResponseDTO login(AuthDtos.LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow();

        return AuthDtos.AuthResponseDTO.builder()
                .token(token)
                .user(AuthDtos.mapToUserDTO(user))
                .build();
    }

    @Transactional
    public AuthDtos.AuthResponseDTO register(AuthDtos.RegisterRequestDTO registerRequest) {
        if(userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new BadRequestException("Email address already in use.");
        }

        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setProvider(AuthProvider.LOCAL);

        User result = userRepository.save(user);

        // Auto login after register
        return login(new AuthDtos.LoginRequestDTO(registerRequest.getEmail(), registerRequest.getPassword()));
    }
}
