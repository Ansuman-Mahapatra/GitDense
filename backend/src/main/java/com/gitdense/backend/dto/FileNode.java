package com.gitdense.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class FileNode {
    private String name;
    private String path;
    private String type; // "FILE" or "DIRECTORY"
    private List<FileNode> children;
}
