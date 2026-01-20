import 'package:dio/dio.dart';
import 'package:frontend/core/constants/app_constants.dart';
import 'package:frontend/core/network/dio_client.dart';
import 'package:frontend/data/models/user_model.dart';
import 'package:logger/logger.dart';

class AuthRemoteDataSource {
  final DioClient _dioClient;
  final Logger _logger = Logger();

  AuthRemoteDataSource(this._dioClient);

  Future<Map<String, dynamic>> login(String email, String password) async {
    try {
      final response = await _dioClient.dio.post(
        AppConstants.loginEndpoint,
        data: {'email': email, 'password': password},
      );
      return response.data;
    } catch (e) {
      _logger.e('Login failed', error: e);
      rethrow;
    }
  }

  Future<Map<String, dynamic>> register(String name, String email, String password) async {
    try {
      final response = await _dioClient.dio.post(
        AppConstants.registerEndpoint,
        data: {'name': name, 'email': email, 'password': password},
      );
      return response.data;
    } catch (e) {
      _logger.e('Register failed', error: e);
      rethrow;
    }
  }

  Future<UserModel> me() async {
    try {
      final response = await _dioClient.dio.get(AppConstants.meEndpoint);
      return UserModel.fromJson(response.data);
    } catch (e) {
      _logger.e('Get user failed', error: e);
      rethrow;
    }
  }
}
