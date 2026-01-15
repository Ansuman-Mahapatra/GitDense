import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/core/utils/secure_storage.dart';
import 'package:frontend/data/datasources/auth_remote_data_source.dart';
import 'package:frontend/data/models/user_model.dart';
import 'package:frontend/data/repositories/auth_repository_impl.dart';
import 'package:frontend/domain/repositories/auth_repository.dart';
import 'package:frontend/presentation/providers/dio_provider.dart';

final authRemoteDataSourceProvider = Provider<AuthRemoteDataSource>((ref) {
  final dioClient = ref.watch(dioClientProvider);
  return AuthRemoteDataSource(dioClient);
});

final authRepositoryProvider = Provider<AuthRepository>((ref) {
  final dataSource = ref.watch(authRemoteDataSourceProvider);
  return AuthRepositoryImpl(dataSource);
});

// Auth State
class AuthState {
  final bool isLoading;
  final bool isAuthenticated;
  final UserModel? user;
  final String? error;

  AuthState({
    this.isLoading = false,
    this.isAuthenticated = false,
    this.user,
    this.error,
  });

  AuthState copyWith({
    bool? isLoading,
    bool? isAuthenticated,
    UserModel? user,
    String? error,
  }) {
    return AuthState(
      isLoading: isLoading ?? this.isLoading,
      isAuthenticated: isAuthenticated ?? this.isAuthenticated,
      user: user ?? this.user,
      error: error,
    );
  }
}

class AuthNotifier extends StateNotifier<AuthState> {
  final AuthRepository _repository;
  final SecureStorage _secureStorage;

  AuthNotifier(this._repository, this._secureStorage) : super(AuthState()) {
    checkAuthStatus();
  }

  Future<void> checkAuthStatus() async {
    final token = await _secureStorage.getToken();
    if (token != null) {
      try {
        final user = await _repository.me();
        state = state.copyWith(isAuthenticated: true, user: user);
      } catch (e) {
        // Token invalid
        await _secureStorage.deleteToken();
        state = state.copyWith(isAuthenticated: false, user: null);
      }
    } else {
      state = state.copyWith(isAuthenticated: false, user: null);
    }
  }

  Future<void> login(String email, String password) async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      final response = await _repository.login(email, password);
      final token = response['token'];
      final user = UserModel.fromJson(response['user']);
      
      await _secureStorage.saveToken(token);
      state = state.copyWith(isLoading: false, isAuthenticated: true, user: user);
    } catch (e) {
      state = state.copyWith(isLoading: false, error: e.toString());
    }
  }

  Future<void> register(String name, String email, String password) async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      final response = await _repository.register(name, email, password);
      final token = response['token'];
      final user = UserModel.fromJson(response['user']);

      await _secureStorage.saveToken(token);
      state = state.copyWith(isLoading: false, isAuthenticated: true, user: user);
    } catch (e) {
      state = state.copyWith(isLoading: false, error: e.toString());
    }
  }

  Future<void> logout() async {
    await _secureStorage.deleteToken();
    state = AuthState();
  }
}

final authProvider = StateNotifierProvider<AuthNotifier, AuthState>((ref) {
  final repository = ref.watch(authRepositoryProvider);
  final secureStorage = ref.watch(secureStorageProvider);
  return AuthNotifier(repository, secureStorage);
});
