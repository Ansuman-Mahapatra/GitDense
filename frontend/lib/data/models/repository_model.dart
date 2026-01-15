class RepositoryModel {
  final String id;
  final String name;
  final String localPath;
  final DateTime? lastAnalyzed;

  RepositoryModel({
    required this.id,
    required this.name,
    required this.localPath,
    this.lastAnalyzed,
  });

  factory RepositoryModel.fromJson(Map<String, dynamic> json) {
    return RepositoryModel(
      id: json['id'],
      name: json['name'],
      localPath: json['localPath'],
      lastAnalyzed: json['lastAnalyzed'] != null
          ? DateTime.parse(json['lastAnalyzed'])
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'localPath': localPath,
      'lastAnalyzed': lastAnalyzed?.toIso8601String(),
    };
  }
}
