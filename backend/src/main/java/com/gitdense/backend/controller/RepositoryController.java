package com.gitdense.backend.controller;

import com.gitdense.backend.dto.CreateRepositoryRequestDTO;
import com.gitdense.backend.dto.RepositoryDTO;
import com.gitdense.backend.security.UserPrincipal;
import com.gitdense.backend.service.RepositoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/repositories")
public class RepositoryController {

    @Autowired
    private RepositoryService repositoryService;

    @GetMapping
    public ResponseEntity<List<RepositoryDTO>> getUserRepositories(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(repositoryService.getAllRepositories(userPrincipal.getId()));
    }

    @PostMapping
    public ResponseEntity<RepositoryDTO> addRepository(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                       @Valid @RequestBody CreateRepositoryRequestDTO request) {
        return ResponseEntity.ok(repositoryService.addRepository(userPrincipal.getId(), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepositoryDTO> getRepository(@PathVariable UUID id) {
        // In real app, verify user owns repo
        // return ResponseEntity.ok(repositoryService.getRepositoryById(id));
        return ResponseEntity.ok().build(); // Implement actual retrieval later
    }
}
