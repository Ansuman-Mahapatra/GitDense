import 'package:dio/dio.dart';
import 'package:frontend/core/constants/app_constants.dart';
import 'package:frontend/core/network/dio_client.dart';
import 'package:frontend/data/models/repository_model.dart';
import 'package:logger/logger.dart';

class RepositoryRemoteDataSource {
  final DioClient _dioClient;
  final Logger _logger = Logger();

  RepositoryRemoteDataSource(this._dioClient);

  Future<List<RepositoryModel>> getRepositories() async {
    try {
      final response = await _dioClient.dio.get(AppConstants.repositoriesEndpoint);
      final List<dynamic> list = response.data;
      return list.map((e) => RepositoryModel.fromJson(e)).toList();
    } catch (e) {
      _logger.e('Get repositories failed', error: e);
      rethrow;
    }
  }

  Future<RepositoryModel> addRepository(String name, String localPath) async {
    try {
      final response = await _dioClient.dio.post(
        AppConstants.repositoriesEndpoint,
        data: {'name': name, 'localPath': localPath},
      );
      return RepositoryModel.fromJson(response.data);
    } catch (e) {
      _logger.e('Add repository failed', error: e);
      rethrow;
    }
  }
}
