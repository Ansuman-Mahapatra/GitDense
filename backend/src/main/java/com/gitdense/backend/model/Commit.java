package com.gitdense.backend.model;

import com.gitdense.backend.model.enums.CommitCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "commits", indexes = {
        @Index(name = "idx_commit_hash", columnList = "commitHash"),
        @Index(name = "idx_repo_date", columnList = "repository_id, commitDate")
})
@Getter
@Setter
public class Commit extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repository_id", nullable = false)
    private Repository repository;

    @Column(nullable = false)
    private String commitHash;

    @Column(columnDefinition = "TEXT")
    private String message;

    private String author;

    @Column(nullable = false)
    private LocalDateTime commitDate;

    private int filesChanged;

    @Enumerated(EnumType.STRING)
    private CommitCategory category;
}
