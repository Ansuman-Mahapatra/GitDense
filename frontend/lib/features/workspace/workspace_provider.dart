import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/data/models/commit_log_entry.dart';
import 'package:frontend/data/models/file_node.dart';
import 'package:frontend/data/repositories/git_repository.dart';

final commitLogProvider = FutureProvider.family<List<CommitLogEntry>, String>((ref, repoId) async {
  final gitRepository = ref.read(gitRepositoryProvider);
  return gitRepository.getCommitLog(repoId);
});

final fileTreeProvider = FutureProvider.family<List<FileNode>, String>((ref, repoId) async {
  final gitRepository = ref.read(gitRepositoryProvider);
  return gitRepository.getFileTree(repoId);
});

final fileContentProvider = FutureProvider.family<String, ({String repoId, String path})>((ref, params) async {
  final gitRepository = ref.read(gitRepositoryProvider);
  return gitRepository.getFileContent(params.repoId, params.path);
});

// State for currently selected file
final selectedFileProvider = StateProvider<String?>((ref) => null);
