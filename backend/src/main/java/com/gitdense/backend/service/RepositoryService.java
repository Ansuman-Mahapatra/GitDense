package com.gitdense.backend.service;

import com.gitdense.backend.dto.CreateRepositoryRequestDTO;
import com.gitdense.backend.dto.RepositoryDTO;
import com.gitdense.backend.exception.ResourceNotFoundException;
import com.gitdense.backend.exception.BadRequestException;
import com.gitdense.backend.model.Repository;
import com.gitdense.backend.model.User;
import com.gitdense.backend.repository.RepositoryRepository;
import com.gitdense.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RepositoryService {

    @Autowired
    private RepositoryRepository repositoryRepository;

    @Autowired
    private UserRepository userRepository;

    public List<RepositoryDTO> getAllRepositories(UUID userId) {
        return repositoryRepository.findByUserId(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public RepositoryDTO addRepository(UUID userId, CreateRepositoryRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        File file = new File(request.getLocalPath());
        if (!file.exists() || !file.isDirectory()) {
            throw new BadRequestException("Invalid repository path. Please ensure the directory exists.");
        }
        
        File gitDir = new File(file, ".git");
         if (!gitDir.exists() || !gitDir.isDirectory()) {
            throw new BadRequestException("Path is not a valid git repository (no .git folder found).");
        }

        Repository repository = new Repository();
        repository.setUser(user);
        repository.setName(request.getName());
        repository.setLocalPath(request.getLocalPath());

        Repository saved = repositoryRepository.save(repository);
        return mapToDTO(saved);
    }
    
    public Repository getRepositoryById(UUID repoId) {
        return repositoryRepository.findById(repoId)
            .orElseThrow(() -> new ResourceNotFoundException("Repository", "id", repoId));
    }

    private RepositoryDTO mapToDTO(Repository repository) {
        return RepositoryDTO.builder()
                .id(repository.getId().toString())
                .name(repository.getName())
                .localPath(repository.getLocalPath())
                .lastAnalyzed(repository.getLastAnalyzed())
                .createdAt(repository.getCreatedAt())
                .build();
    }
}
