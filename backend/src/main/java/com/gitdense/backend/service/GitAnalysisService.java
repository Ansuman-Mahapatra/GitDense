package com.gitdense.backend.service;

import com.gitdense.backend.model.Commit;
import com.gitdense.backend.model.Repository;
import com.gitdense.backend.model.enums.CommitCategory;
import com.gitdense.backend.repository.CommitRepository;
import com.gitdense.backend.repository.RepositoryRepository;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class GitAnalysisService {

    @Autowired
    private CommitRepository commitRepository;

    @Autowired
    private RepositoryRepository repositoryRepository;

    @Autowired
    private MLAnalysisService mlAnalysisService;

    @Transactional
    public void analyzeRepository(Repository repository) throws Exception {
        File repoFile = new File(repository.getLocalPath());
        try (Git git = Git.open(repoFile)) {
            Iterable<RevCommit> commits = git.log().call();
            List<Commit> newCommits = new ArrayList<>();

            for (RevCommit revCommit : commits) {
               // Check if commit exists
               String hash = revCommit.getName();
               // Optimization: In real app, check DB existence efficiently or rely on unique constraint
               
               Commit commit = new Commit();
               commit.setRepository(repository);
               commit.setCommitHash(hash);
               commit.setMessage(revCommit.getFullMessage());
               
               PersonIdent authorIdent = revCommit.getAuthorIdent();
               commit.setAuthor(authorIdent.getName());
               
               Date authorDate = authorIdent.getWhen();
               commit.setCommitDate(LocalDateTime.ofInstant(authorDate.toInstant(), ZoneId.systemDefault()));
               
               // Files changed count (Placeholder - requires diff formatter)
               commit.setFilesChanged(1); 
               
               // AI Categorization
               commit.setCategory(mlAnalysisService.categorizeCommit(commit));
               
               newCommits.add(commit);
               
               if(newCommits.size() >= 100) {
                   commitRepository.saveAll(newCommits);
                   newCommits.clear();
               }
            }
             if(!newCommits.isEmpty()) {
                   commitRepository.saveAll(newCommits);
            }
            
            repository.setLastAnalyzed(LocalDateTime.now());
            repositoryRepository.save(repository);
        }
    }
}
