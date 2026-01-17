package com.gitdense.backend.service;

import com.gitdense.backend.dto.CommitLogEntry;
import com.gitdense.backend.dto.RepositoryDTO;
import com.gitdense.backend.model.Repository;
import com.gitdense.backend.repository.RepositoryRepository;
import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GitInspectorService {

    private final RepositoryRepository repositoryRepository;
    private final com.gitdense.backend.repository.UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<RepositoryDTO> getAllRepositories() {
        return repositoryRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public RepositoryDTO addRepository(String path, String name) {
        File repoDir = new File(path);
        if (!repoDir.exists()) {
             throw new IllegalArgumentException("Path does not exist: " + path);
        }
        
        // Verify it is a git repo
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        try (org.eclipse.jgit.lib.Repository repository = builder.setGitDir(new File(repoDir, ".git"))
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .build()) {
            
            if (repository.getDirectory() == null || !repository.getDirectory().exists()) {
                 throw new IllegalArgumentException("Not a valid git repository: " + path);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to read git repository at " + path, e);
        }

        Repository repo = new Repository();
        repo.setLocalPath(path);
        repo.setName(name);
        
        // Ensure a default user exists for this local instance
        com.gitdense.backend.model.User user = userRepository.findByEmail("local@gitdense.com")
                .orElseGet(() -> {
                    com.gitdense.backend.model.User newUser = new com.gitdense.backend.model.User();
                    newUser.setEmail("local@gitdense.com");
                    newUser.setName("Local User");
                    newUser.setPassword("local"); // Not used for local auth but required?
                    // Role and provider are set by default constructor, but ensure they're set
                    if (newUser.getRole() == null) {
                        newUser.setRole(com.gitdense.backend.model.enums.Role.USER);
                    }
                    if (newUser.getProvider() == null) {
                        newUser.setProvider(com.gitdense.backend.model.enums.AuthProvider.LOCAL);
                    }
                    return userRepository.save(newUser);
                });
        repo.setUser(user);
        
        return mapToDTO(repositoryRepository.save(repo));
        }

    public List<CommitLogEntry> getCommitLog(UUID repoId) {
        Repository repoEntity = repositoryRepository.findById(repoId)
                .orElseThrow(() -> new IllegalArgumentException("Repository not found with ID: " + repoId));

        List<CommitLogEntry> commits = new ArrayList<>();
        try (Git git = Git.open(new File(repoEntity.getLocalPath()))) {
            Iterable<RevCommit> log = git.log().all().call();
            for (RevCommit commit : log) {
                commits.add(mapToCommitDTO(commit));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to get commit log", e);
        }
        return commits;
    }

    private CommitLogEntry mapToCommitDTO(RevCommit commit) {
        List<String> parents = new ArrayList<>();
        for (RevCommit parent : commit.getParents()) {
            parents.add(parent.getName());
        }

        return CommitLogEntry.builder()
                .hash(commit.getName())
                .shortHash(commit.getName().substring(0, 7))
                .message(commit.getFullMessage())
                .authorName(commit.getAuthorIdent().getName())
                .authorEmail(commit.getAuthorIdent().getEmailAddress())
                .commitTime(LocalDateTime.ofInstant(Instant.ofEpochSecond(commit.getCommitTime()), ZoneId.systemDefault()))
                .parentHashes(parents)
                .build();
    }

    private RepositoryDTO mapToDTO(Repository repo) {
        return RepositoryDTO.builder()
                .id(repo.getId().toString())
                .name(repo.getName())
                .localPath(repo.getLocalPath())
                .lastAnalyzed(repo.getLastAnalyzed())
                .createdAt(repo.getCreatedAt())
                .build();
    }

    public List<com.gitdense.backend.dto.FileNode> getFileTree(UUID repoId, String commitHash) {
        Repository repoEntity = repositoryRepository.findById(repoId)
                .orElseThrow(() -> new IllegalArgumentException("Repository not found"));

        try (Git git = Git.open(new File(repoEntity.getLocalPath()))) {
            ObjectId treeId;
            if (commitHash == null || commitHash.equals("HEAD")) {
                treeId = git.getRepository().resolve("HEAD^{tree}");
            } else {
                 treeId = git.getRepository().resolve(commitHash + "^{tree}");
            }
            
            // JGit TreeWalk
            org.eclipse.jgit.lib.Repository jgitRepo = git.getRepository();
            try (org.eclipse.jgit.treewalk.TreeWalk treeWalk = new org.eclipse.jgit.treewalk.TreeWalk(jgitRepo)) {
                treeWalk.addTree(treeId);
                treeWalk.setRecursive(false);
                
                return walkTree(treeWalk, jgitRepo);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to get file tree", e);
        }
    }
    
    // Recursive helper to build tree
    private List<com.gitdense.backend.dto.FileNode> walkTree(org.eclipse.jgit.treewalk.TreeWalk treeWalk, org.eclipse.jgit.lib.Repository repository) throws IOException {
        List<com.gitdense.backend.dto.FileNode> nodes = new ArrayList<>();
        while (treeWalk.next()) {
             boolean isSubtree = treeWalk.isSubtree();
             com.gitdense.backend.dto.FileNode.FileNodeBuilder nodeBuilder = com.gitdense.backend.dto.FileNode.builder()
                     .name(treeWalk.getNameString())
                     .path(treeWalk.getPathString())
                     .type(isSubtree ? "DIRECTORY" : "FILE");
             
             if (isSubtree) {
                 // Create a new TreeWalk for the subtree
                 org.eclipse.jgit.treewalk.TreeWalk subWalk = new org.eclipse.jgit.treewalk.TreeWalk(repository);
                 subWalk.addTree(treeWalk.getObjectId(0));
                 subWalk.setRecursive(false);
                 try {
                     nodeBuilder.children(walkTree(subWalk, repository));
                 } finally {
                     subWalk.close();
                 }
             }
             nodes.add(nodeBuilder.build());
        }
        return nodes;
    }

    public String getFileContent(UUID repoId, String commitHash, String userPath) {
        Repository repoEntity = repositoryRepository.findById(repoId)
                .orElseThrow(() -> new IllegalArgumentException("Repository not found"));

        try (Git git = Git.open(new File(repoEntity.getLocalPath()))) {
             ObjectId commitId = git.getRepository().resolve(commitHash != null ? commitHash : "HEAD");
             
             try (org.eclipse.jgit.revwalk.RevWalk revWalk = new org.eclipse.jgit.revwalk.RevWalk(git.getRepository())) {
                 RevCommit commit = revWalk.parseCommit(commitId);
                 
                 try (org.eclipse.jgit.treewalk.TreeWalk treeWalk = new org.eclipse.jgit.treewalk.TreeWalk(git.getRepository())) {
                     treeWalk.addTree(commit.getTree());
                     treeWalk.setRecursive(true);
                     treeWalk.setFilter(org.eclipse.jgit.treewalk.filter.PathFilter.create(userPath));
                     
                     if (!treeWalk.next()) {
                         throw new IllegalArgumentException("File not found: " + userPath);
                     }
                     
                     ObjectId objectId = treeWalk.getObjectId(0);
                     org.eclipse.jgit.lib.ObjectLoader loader = git.getRepository().open(objectId);
                     
                     // Check size to prevent OOM
                     if (loader.getSize() > 10 * 1024 * 1024) { // 10MB limit
                         return "File too large to display";
                     }
                     return new String(loader.getBytes());
                 }
             }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read file content", e);
        }
    }

    @Transactional
    public void checkout(UUID repoId, String refName) {
        Repository repoEntity = repositoryRepository.findById(repoId)
                .orElseThrow(() -> new IllegalArgumentException("Repository not found"));

        try (Git git = Git.open(new File(repoEntity.getLocalPath()))) {
            git.checkout().setName(refName).call();
        } catch (Exception e) {
             throw new RuntimeException("Failed to checkout " + refName, e);
        }
    }

    @Transactional
    public void createBranch(UUID repoId, String branchName) {
        Repository repoEntity = repositoryRepository.findById(repoId)
                 .orElseThrow(() -> new IllegalArgumentException("Repository not found"));

         try (Git git = Git.open(new File(repoEntity.getLocalPath()))) {
             git.branchCreate().setName(branchName).call();
         } catch (Exception e) {
              throw new RuntimeException("Failed to create branch " + branchName, e);
         }
    }

    @Transactional
    public String commitChanges(UUID repoId, String message, String authorName, String authorEmail) {
        Repository repoEntity = repositoryRepository.findById(repoId)
                 .orElseThrow(() -> new IllegalArgumentException("Repository not found"));

         try (Git git = Git.open(new File(repoEntity.getLocalPath()))) {
             // Stage all changes (Git Add -A)
             git.add().addFilepattern(".").call();
             
             // Commit
             RevCommit commit = git.commit()
                     .setMessage(message)
                     .setAuthor(authorName, authorEmail)
                     .call();
             
             return commit.getName();
         } catch (Exception e) {
              throw new RuntimeException("Failed to commit changes", e);
         }
    }
}
