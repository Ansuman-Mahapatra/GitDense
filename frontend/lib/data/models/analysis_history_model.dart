class AnalysisHistoryModel {
  final String id;
  final String repositoryId;
  final DateTime analyzedAt;
  final int totalCommits;
  final Map<String, int> categoriesCount;
  final int executionTimeMs;

  AnalysisHistoryModel({
    required this.id,
    required this.repositoryId,
    required this.analyzedAt,
    required this.totalCommits,
    required this.categoriesCount,
    required this.executionTimeMs,
  });

  factory AnalysisHistoryModel.fromJson(Map<String, dynamic> json) {
    return AnalysisHistoryModel(
      id: json['id'],
      repositoryId: json['repositoryId'],
      analyzedAt: DateTime.parse(json['analyzedAt']),
      totalCommits: json['totalCommits'],
      categoriesCount: Map<String, int>.from(json['categoriesCount']),
      executionTimeMs: json['executionTimeMs'],
    );
  }
}
