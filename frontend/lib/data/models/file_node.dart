class FileNode {
  final String name;
  final String path;
  final String type; // "FILE" or "DIRECTORY"
  final List<FileNode>? children;

  FileNode({
    required this.name,
    required this.path,
    required this.type,
    this.children,
  });

  factory FileNode.fromJson(Map<String, dynamic> json) {
    return FileNode(
      name: json['name'] as String,
      path: json['path'] as String,
      type: json['type'] as String,
      children: json['children'] != null
          ? (json['children'] as List<dynamic>)
              .map((e) => FileNode.fromJson(e as Map<String, dynamic>))
              .toList()
          : null,
    );
  }
}
