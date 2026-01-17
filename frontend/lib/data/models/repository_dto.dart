class RepositoryDTO {
  final String id;
  final String name;
  final String localPath;
  final DateTime? lastAnalyzed;

  RepositoryDTO({
    required this.id,
    required this.name,
    required this.localPath,
    this.lastAnalyzed,
  });

  factory RepositoryDTO.fromJson(Map<String, dynamic> json) {
    return RepositoryDTO(
      id: json['id'] as String,
      name: json['name'] as String,
      localPath: json['localPath'] as String,
      lastAnalyzed: json['lastAnalyzed'] != null
          ? DateTime.parse(json['lastAnalyzed'] as String)
          : null,
    );
  }
}
