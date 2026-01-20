import 'package:frontend/data/datasources/repository_remote_data_source.dart';
import 'package:frontend/data/models/repository_model.dart';
import 'package:frontend/domain/repositories/repository_repository.dart';

class RepositoryRepositoryImpl implements RepositoryRepository {
  final RepositoryRemoteDataSource _remoteDataSource;

  RepositoryRepositoryImpl(this._remoteDataSource);

  @override
  Future<List<RepositoryModel>> getRepositories() {
    return _remoteDataSource.getRepositories();
  }

  @override
  Future<RepositoryModel> addRepository(String name, String localPath) {
    return _remoteDataSource.addRepository(name, localPath);
  }
}
