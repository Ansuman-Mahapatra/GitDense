import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/data/models/file_node.dart';
import 'package:frontend/features/workspace/workspace_provider.dart';

class FileTreeWidget extends ConsumerWidget {
  final String repoId;

  const FileTreeWidget({super.key, required this.repoId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final fileTreeAsync = ref.watch(fileTreeProvider(repoId));

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Padding(
          padding: const EdgeInsets.all(8.0),
          child: Text('FILES', style: Theme.of(context).textTheme.labelSmall),
        ),
        Expanded(
          child: fileTreeAsync.when(
            data: (nodes) => ListView(
              children: nodes.map((node) => _FileNodeTile(node: node, repoId: repoId)).toList(),
            ),
            loading: () => const Center(child: CircularProgressIndicator()),
            error: (err, stack) => Center(child: Text('Error: $err')),
          ),
        ),
      ],
    );
  }
}

class _FileNodeTile extends ConsumerWidget {
  final FileNode node;
  final String repoId;
  final double padding;

  const _FileNodeTile({required this.node, required this.repoId, this.padding = 0});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final isDirectory = node.type == 'DIRECTORY';
    final isSelected = ref.watch(selectedFileProvider) == node.path;

    if (!isDirectory) {
      return InkWell(
        onTap: () {
          ref.read(selectedFileProvider.notifier).state = node.path;
        },
        child: Container(
          color: isSelected ? Theme.of(context).colorScheme.primary.withOpacity(0.2) : null,
          padding: EdgeInsets.only(left: 16 + padding, top: 4, bottom: 4),
          child: Row(
            children: [
              const Icon(Icons.insert_drive_file, size: 16, color: Colors.grey),
              const SizedBox(width: 8),
              Expanded(child: Text(node.name, overflow: TextOverflow.ellipsis)),
            ],
          ),
        ),
      );
    }

    return ExpansionTile(
      tilePadding: EdgeInsets.only(left: 16 + padding),
      leading: const Icon(Icons.folder, size: 16, color: Colors.blueGrey),
      title: Text(node.name),
      children: node.children?.map((child) => _FileNodeTile(node: child, repoId: repoId, padding: padding + 16)).toList() ?? [],
    );
  }
}
