class CommitLogEntry {
  final String hash;
  final String shortHash;
  final String message;
  final String authorName;
  final String authorEmail;
  final DateTime commitTime;
  final List<String> parentHashes;

  CommitLogEntry({
    required this.hash,
    required this.shortHash,
    required this.message,
    required this.authorName,
    required this.authorEmail,
    required this.commitTime,
    required this.parentHashes,
  });

  factory CommitLogEntry.fromJson(Map<String, dynamic> json) {
    return CommitLogEntry(
      hash: json['hash'] as String,
      shortHash: json['shortHash'] as String,
      message: json['message'] as String,
      authorName: json['authorName'] as String,
      authorEmail: json['authorEmail'] as String,
      commitTime: DateTime.parse(json['commitTime'] as String),
      parentHashes: (json['parentHashes'] as List<dynamic>).cast<String>(),
    );
  }
}
