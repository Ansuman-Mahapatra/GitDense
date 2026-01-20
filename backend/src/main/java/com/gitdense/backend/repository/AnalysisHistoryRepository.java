package com.gitdense.backend.repository;

import com.gitdense.backend.model.AnalysisHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnalysisHistoryRepository extends JpaRepository<AnalysisHistory, UUID> {
    List<AnalysisHistory> findByRepository_IdOrderByAnalyzedAtDesc(UUID repositoryId);
}
