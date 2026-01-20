class CommitModel {
  final String hash;
  final String message;
  final String author;
  final DateTime date;
  final int filesChanged;
  final String category;

  CommitModel({
    required this.hash,
    required this.message,
    required this.author,
    required this.date,
    required this.filesChanged,
    required this.category,
  });

  factory CommitModel.fromJson(Map<String, dynamic> json) {
    return CommitModel(
      hash: json['hash'],
      message: json['message'],
      author: json['author'],
      date: DateTime.parse(json['date']),
      filesChanged: json['filesChanged'],
      category: json['category'],
    );
  }
}
