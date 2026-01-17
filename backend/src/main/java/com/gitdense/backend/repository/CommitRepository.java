package com.gitdense.backend.repository;

import com.gitdense.backend.model.Commit;
import com.gitdense.backend.model.enums.CommitCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CommitRepository extends JpaRepository<Commit, UUID> {
    
    Page<Commit> findByRepository_Id(UUID repositoryId, Pageable pageable);
    
    Page<Commit> findByRepository_IdAndCategory(UUID repositoryId, CommitCategory category, Pageable pageable);
    
    List<Commit> findByRepository_IdAndCommitDateBetween(UUID repositoryId, LocalDateTime start, LocalDateTime end);
    
    long countByRepository_Id(UUID repositoryId);
    
    @Query("SELECT c.category, COUNT(c) FROM Commit c WHERE c.repository.id = :repositoryId GROUP BY c.category")
    List<Object[]> countCommitsByCategory(@Param("repositoryId") UUID repositoryId);
}
