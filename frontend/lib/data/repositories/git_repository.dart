import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/data/models/repository_dto.dart';

import 'package:frontend/data/models/commit_log_entry.dart';

final dioProvider = Provider((ref) => Dio(BaseOptions(baseUrl: 'http://localhost:8080/api/git')));

final gitRepositoryProvider = Provider((ref) => GitRepository(ref.read(dioProvider)));

class GitRepository {
  final Dio _dio;

  GitRepository(this._dio);

  Future<List<RepositoryDTO>> getRepositories() async {
    try {
      final response = await _dio.get('/repos');
      final List<dynamic> data = response.data;
      return data.map((json) => RepositoryDTO.fromJson(json)).toList();
    } catch (e) {
      throw Exception('Failed to fetch repositories: $e');
    }
  }

  Future<RepositoryDTO> addRepository(String path, String name) async {
    try {
      final response = await _dio.post('/repos', data: {
        'path': path,
        'name': name,
      });
      return RepositoryDTO.fromJson(response.data);
    } catch (e) {
      throw Exception('Failed to add repository: $e');
    }
  }

import 'package:frontend/data/models/file_node.dart';

  Future<List<CommitLogEntry>> getCommitLog(String repoId) async {
    try {
      final response = await _dio.get('/$repoId/log');
      final List<dynamic> data = response.data;
      return data.map((json) => CommitLogEntry.fromJson(json)).toList();
    } catch (e) {
      throw Exception('Failed to fetch commit log: $e');
    }
  }

  Future<List<FileNode>> getFileTree(String repoId, {String commit = 'HEAD'}) async {
    try {
      final response = await _dio.get('/$repoId/tree', queryParameters: {'commit': commit});
      final List<dynamic> data = response.data;
      return data.map((json) => FileNode.fromJson(json)).toList();
    } catch (e) {
      throw Exception('Failed to fetch file tree: $e');
    }
  }

  Future<String> getFileContent(String repoId, String path, {String commit = 'HEAD'}) async {
    try {
      final response = await _dio.get('/$repoId/blob', queryParameters: {'path': path, 'commit': commit});
      return response.data as String;
    } catch (e) {
      throw Exception('Failed to fetch file content: $e');
    }
  }

  Future<void> checkout(String repoId, String ref) async {
    try {
      await _dio.post('/$repoId/checkout', data: {'ref': ref});
    } catch (e) {
      throw Exception('Failed to checkout: $e');
    }
  }

  Future<void> createBranch(String repoId, String name) async {
    try {
      await _dio.post('/$repoId/branch', data: {'name': name});
    } catch (e) {
      throw Exception('Failed to create branch: $e');
    }
  }

  Future<String> commit(String repoId, String message) async {
    try {
      final response = await _dio.post('/$repoId/commit', data: {'message': message});
      return response.data as String;
    } catch (e) {
      throw Exception('Failed to commit: $e');
    }
  }
}
