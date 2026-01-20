import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/data/datasources/repository_remote_data_source.dart';
import 'package:frontend/data/models/repository_model.dart';
import 'package:frontend/data/repositories/repository_repository_impl.dart';
import 'package:frontend/domain/repositories/repository_repository.dart';
import 'package:frontend/presentation/providers/dio_provider.dart';

final repositoryRemoteDataSourceProvider = Provider<RepositoryRemoteDataSource>((ref) {
  final dioClient = ref.watch(dioClientProvider);
  return RepositoryRemoteDataSource(dioClient);
});

final repositoryRepositoryProvider = Provider<RepositoryRepository>((ref) {
  final dataSource = ref.watch(repositoryRemoteDataSourceProvider);
  return RepositoryRepositoryImpl(dataSource);
});

final repositoryListProvider = FutureProvider.autoDispose<List<RepositoryModel>>((ref) async {
  final repository = ref.watch(repositoryRepositoryProvider);
  return repository.getRepositories();
});

// A provider to handle adding requests
class RepositoryNotifier extends StateNotifier<AsyncValue<void>> {
  final RepositoryRepository _repository;
  final Ref _ref;

  RepositoryNotifier(this._repository, this._ref) : super(const AsyncValue.data(null));

  Future<void> addRepository(String name, String localPath) async {
    state = const AsyncValue.loading();
    try {
      await _repository.addRepository(name, localPath);
      // Invalidate the list provider to refetch
      _ref.invalidate(repositoryListProvider);
      state = const AsyncValue.data(null);
    } catch (e, st) {
      state = AsyncValue.error(e, st);
    }
  }
}

final repositoryNotifierProvider = StateNotifierProvider<RepositoryNotifier, AsyncValue<void>>((ref) {
  final repository = ref.watch(repositoryRepositoryProvider);
  return RepositoryNotifier(repository, ref);
});
