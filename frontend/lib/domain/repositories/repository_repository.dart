import 'package:frontend/data/models/repository_model.dart';

abstract class RepositoryRepository {
  Future<List<RepositoryModel>> getRepositories();
  Future<RepositoryModel> addRepository(String name, String localPath);
}
