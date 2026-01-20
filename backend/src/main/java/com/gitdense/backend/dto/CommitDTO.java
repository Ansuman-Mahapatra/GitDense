package com.gitdense.backend.dto;

import com.gitdense.backend.model.enums.CommitCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommitDTO {
    private String hash;
    private String message;
    private String author;
    private LocalDateTime date;
    private int filesChanged;
    private CommitCategory category;
}
