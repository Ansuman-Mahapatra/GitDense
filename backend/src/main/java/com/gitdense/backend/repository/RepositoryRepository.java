package com.gitdense.backend.repository;

import com.gitdense.backend.model.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@org.springframework.stereotype.Repository
public interface RepositoryRepository extends JpaRepository<Repository, UUID> {
    List<Repository> findByUserId(UUID userId);
    Optional<Repository> findByLocalPath(String localPath);
}
