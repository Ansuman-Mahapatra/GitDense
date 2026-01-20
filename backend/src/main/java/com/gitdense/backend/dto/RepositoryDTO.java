package com.gitdense.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepositoryDTO {
    private String id;
    private String name;
    private String localPath;
    private LocalDateTime lastAnalyzed;
    private LocalDateTime createdAt;
}

