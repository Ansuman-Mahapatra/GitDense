package com.gitdense.backend.service;

import com.gitdense.backend.model.Commit;
import com.gitdense.backend.model.enums.CommitCategory;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class MLAnalysisService {
    
    // Placeholder for OpenAI API integration
    public CommitCategory categorizeCommit(Commit commit) {
        String message = commit.getMessage().toLowerCase(Locale.ROOT);
        
        if (message.contains("fix") || message.contains("bug")) {
            return CommitCategory.BUGFIX;
        } else if (message.contains("feat") || message.contains("add") || message.contains("new")) {
            return CommitCategory.FEATURE;
        } else if (message.contains("test")) {
            return CommitCategory.TEST;
        } else if (message.contains("refactor") || message.contains("improve")) {
            return CommitCategory.REFACTOR;
        } else if (message.contains("doc")) {
            return CommitCategory.DOCS;
        } else if (message.contains("chore")) {
            return CommitCategory.CHORE;
        }
        
        return CommitCategory.UNKNOWN;
    }
}
