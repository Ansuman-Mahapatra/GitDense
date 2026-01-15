class AppConstants {
  static const String baseUrl = 'http://localhost:8080/api';
  
  // Auth Endpoints
  static const String loginEndpoint = '/auth/login';
  static const String registerEndpoint = '/auth/register';
  static const String meEndpoint = '/auth/me';
  
  // Repository Endpoints
  static const String repositoriesEndpoint = '/repositories';
  static const String startAnalysisEndpoint = '/analysis/repository/{id}/start';
  static const String analysisHistoryEndpoint = '/analysis/repository/{id}/history';
  
  // Storage Keys
  static const String tokenKey = 'jwt_token';
}
