package com.gitdense.backend.controller;

import com.gitdense.backend.dto.CommitLogEntry;
import com.gitdense.backend.dto.RepositoryDTO;
import com.gitdense.backend.service.GitInspectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/git")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Allow Flutter app to connect
public class GitInspectorController {

    private final GitInspectorService gitInspectorService;

    @GetMapping("/repos")
    public ResponseEntity<List<RepositoryDTO>> getAllRepositories() {
        return ResponseEntity.ok(gitInspectorService.getAllRepositories());
    }

    @PostMapping("/repos")
    public ResponseEntity<RepositoryDTO> addRepository(@RequestBody Map<String, String> payload) {
        String path = payload.get("path");
        String name = payload.get("name");
        return ResponseEntity.ok(gitInspectorService.addRepository(path, name));
    }

    @GetMapping("/{repoId}/log")
    public ResponseEntity<List<CommitLogEntry>> getCommitLog(@PathVariable UUID repoId) {
        return ResponseEntity.ok(gitInspectorService.getCommitLog(repoId));
    }

    @GetMapping("/{repoId}/tree")
    public ResponseEntity<List<com.gitdense.backend.dto.FileNode>> getFileTree(
            @PathVariable UUID repoId,
            @RequestParam(required = false, defaultValue = "HEAD") String commit) {
        return ResponseEntity.ok(gitInspectorService.getFileTree(repoId, commit));
    }

    @GetMapping("/{repoId}/blob")
    public ResponseEntity<String> getFileContent(
            @PathVariable UUID repoId,
            @RequestParam String path,
            @RequestParam(required = false, defaultValue = "HEAD") String commit) {
        return ResponseEntity.ok(gitInspectorService.getFileContent(repoId, commit, path));
    }

    @PostMapping("/{repoId}/checkout")
    public ResponseEntity<Void> checkout(@PathVariable UUID repoId, @RequestBody Map<String, String> payload) {
        String refName = payload.get("ref");
        gitInspectorService.checkout(repoId, refName);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{repoId}/branch")
    public ResponseEntity<Void> createBranch(@PathVariable UUID repoId, @RequestBody Map<String, String> payload) {
        String branchName = payload.get("name");
        gitInspectorService.createBranch(repoId, branchName);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{repoId}/commit")
    public ResponseEntity<String> commit(@PathVariable UUID repoId, @RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        String authorName = payload.getOrDefault("authorName", "GitDense User");
        String authorEmail = payload.getOrDefault("authorEmail", "user@gitdense.com");
        return ResponseEntity.ok(gitInspectorService.commitChanges(repoId, message, authorName, authorEmail));
    }
}
