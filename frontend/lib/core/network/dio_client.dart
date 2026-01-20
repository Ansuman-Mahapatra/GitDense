import 'package:dio/dio.dart';
import 'package:frontend/core/constants/app_constants.dart';
import 'package:frontend/core/utils/secure_storage.dart';
import 'package:logger/logger.dart';

class DioClient {
  final Dio _dio;
  final SecureStorage _secureStorage;
  final Logger _logger = Logger();

  DioClient(this._secureStorage)
      : _dio = Dio(
          BaseOptions(
            baseUrl: AppConstants.baseUrl,
            connectTimeout: const Duration(seconds: 10),
            receiveTimeout: const Duration(seconds: 10),
          ),
        ) {
    _dio.interceptors.add(
      InterceptorsWrapper(
        onRequest: (options, handler) async {
          final token = await _secureStorage.getToken();
          if (token != null) {
            options.headers['Authorization'] = 'Bearer $token';
          }
          _logger.i('Request: ${options.method} ${options.path}');
          return handler.next(options);
        },
        onResponse: (response, handler) {
          _logger.i('Response: ${response.statusCode}');
          return handler.next(response);
        },
        onError: (DioException e, handler) {
          _logger.e('Error: ${e.message}', error: e.error, stackTrace: e.stackTrace);
          return handler.next(e);
        },
      ),
    );
  }

  Dio get dio => _dio;
}
