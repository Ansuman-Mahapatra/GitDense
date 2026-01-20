package com.gitdense.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisHistoryDTO {
    private String id;
    private String repositoryId;
    private LocalDateTime analyzedAt;
    private int totalCommits;
    private Map<String, Integer> categoriesCount;
    private String insightsJson;
    private long executionTimeMs;
}

