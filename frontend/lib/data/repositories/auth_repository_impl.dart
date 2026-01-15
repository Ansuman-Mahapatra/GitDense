import 'package:frontend/data/datasources/auth_remote_data_source.dart';
import 'package:frontend/data/models/user_model.dart';
import 'package:frontend/domain/repositories/auth_repository.dart';

class AuthRepositoryImpl implements AuthRepository {
  final AuthRemoteDataSource _remoteDataSource;

  AuthRepositoryImpl(this._remoteDataSource);

  @override
  Future<Map<String, dynamic>> login(String email, String password) {
    return _remoteDataSource.login(email, password);
  }

  @override
  Future<Map<String, dynamic>> register(String name, String email, String password) {
    return _remoteDataSource.register(name, email, password);
  }

  @override
  Future<UserModel> me() {
    return _remoteDataSource.me();
  }
}
