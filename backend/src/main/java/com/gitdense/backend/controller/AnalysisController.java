package com.gitdense.backend.controller;

import com.gitdense.backend.dto.AnalysisHistoryDTO;
import com.gitdense.backend.dto.AnalysisResultDTO;
import com.gitdense.backend.model.Repository;
import com.gitdense.backend.service.AnalysisHistoryService;
import com.gitdense.backend.service.GitAnalysisService;
import com.gitdense.backend.service.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    @Autowired
    private GitAnalysisService gitAnalysisService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private AnalysisHistoryService analysisHistoryService;

    @PostMapping("/repository/{id}/start")
    public ResponseEntity<AnalysisResultDTO> startAnalysis(@PathVariable UUID id) {
        Repository repository = repositoryService.getRepositoryById(id);
        long startTime = System.currentTimeMillis();
        
        try {
            gitAnalysisService.analyzeRepository(repository);
            
            // Capture results (mocked for now, real implementation would aggregate stats)
            analysisHistoryService.saveHistory(repository, 0, null, System.currentTimeMillis() - startTime);
            
            return ResponseEntity.ok(AnalysisResultDTO.builder()
                    .success(true)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Analysis failed: " + e.getMessage());
        }
    }

    @GetMapping("/repository/{id}/history")
    public ResponseEntity<List<AnalysisHistoryDTO>> getAnalysisHistory(@PathVariable UUID id) {
        return ResponseEntity.ok(analysisHistoryService.getHistory(id));
    }
}
