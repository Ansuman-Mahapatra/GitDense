package com.gitdense.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CommitLogEntry {
    private String hash;
    private String shortHash;
    private String message;
    private String authorName;
    private String authorEmail;
    private LocalDateTime commitTime;
    private List<String> parentHashes;
}
