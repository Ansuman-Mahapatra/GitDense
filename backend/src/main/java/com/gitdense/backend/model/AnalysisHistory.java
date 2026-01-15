package com.gitdense.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "analysis_history")
@Getter
@Setter
public class AnalysisHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repository_id", nullable = false)
    private Repository repository;

    @Column(nullable = false)
    private LocalDateTime analyzedAt;

    private int totalCommits;

    @ElementCollection
    @CollectionTable(name = "analysis_categories_count", joinColumns = @JoinColumn(name = "analysis_id"))
    @MapKeyColumn(name = "category")
    @Column(name = "count")
    private Map<String, Integer> categoriesCount;

    @Column(columnDefinition = "TEXT")
    private String insightsJson;

    private long executionTimeMs;
}
