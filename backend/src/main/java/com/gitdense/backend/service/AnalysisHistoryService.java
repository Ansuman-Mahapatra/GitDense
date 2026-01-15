package com.gitdense.backend.service;

import com.gitdense.backend.dto.AnalysisHistoryDTO;
import com.gitdense.backend.model.AnalysisHistory;
import com.gitdense.backend.model.Repository;
import com.gitdense.backend.repository.AnalysisHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AnalysisHistoryService {
    
    @Autowired
    private AnalysisHistoryRepository analysisHistoryRepository;
    
    public void saveHistory(Repository repository, int totalCommits, Map<String, Integer> categoriesCount, long executionTime) {
        AnalysisHistory history = new AnalysisHistory();
        history.setRepository(repository);
        history.setAnalyzedAt(LocalDateTime.now());
        history.setTotalCommits(totalCommits);
        history.setCategoriesCount(categoriesCount);
        history.setExecutionTimeMs(executionTime);
        
        analysisHistoryRepository.save(history);
    }
    
    public List<AnalysisHistoryDTO> getHistory(UUID repositoryId) {
        return analysisHistoryRepository.findByRepositoryIdOrderByAnalyzedAtDesc(repositoryId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    private AnalysisHistoryDTO mapToDTO(AnalysisHistory history) {
        return AnalysisHistoryDTO.builder()
                .id(history.getId().toString())
                .repositoryId(history.getRepository().getId().toString())
                .analyzedAt(history.getAnalyzedAt())
                .totalCommits(history.getTotalCommits())
                .categoriesCount(history.getCategoriesCount())
                .insightsJson(history.getInsightsJson())
                .executionTimeMs(history.getExecutionTimeMs())
                .build();
    }
}
