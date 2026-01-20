import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/data/models/repository_dto.dart';
import 'package:frontend/data/repositories/git_repository.dart';

final dashboardRepositoriesProvider = FutureProvider<List<RepositoryDTO>>((ref) async {
  final gitRepository = ref.read(gitRepositoryProvider);
  return gitRepository.getRepositories();
});
