import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/workspace/widgets/code_editor_widget.dart';
import 'package:frontend/features/workspace/widgets/file_tree_widget.dart';
import 'package:frontend/features/workspace/workspace_provider.dart';
import 'package:frontend/data/repositories/git_repository.dart';

class WorkspacePage extends ConsumerWidget {
  final String repoId;

  const WorkspacePage({super.key, required this.repoId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Workspace'),
        actions: [
          IconButton(
            icon: const Icon(Icons.upload_file), // Commit icon
            tooltip: 'Commit Changes',
            onPressed: () => _showCommitDialog(context, ref),
          ),
          IconButton(icon: const Icon(Icons.settings), onPressed: () {}),
        ],
      ),
      body: Row(
        children: [
          // File Tree
          SizedBox(
            width: 250,
            child: Container(
              decoration: BoxDecoration(
                border: Border(right: BorderSide(color: Theme.of(context).dividerColor)),
              ),
              child: FileTreeWidget(repoId: repoId),
            ),
          ),
          
          // Code Editor
          Expanded(
            flex: 2,
            child: CodeEditorWidget(repoId: repoId),
          ),

          // Git History / Graph
          Container(
             width: 300, 
             decoration: BoxDecoration(
                border: Border(left: BorderSide(color: Theme.of(context).dividerColor)),
              ),
             child: _GitHistoryPanel(repoId: repoId),
          ),
        ],
      ),
    );
  }

  Future<void> _showCommitDialog(BuildContext context, WidgetRef ref) async {
    final messageController = TextEditingController();
    await showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Commit Changes'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            const Text('This will stage and commit all changes.'),
            const SizedBox(height: 16),
            TextField(
              controller: messageController,
              decoration: const InputDecoration(labelText: 'Commit Message'),
              autofocus: true,
              maxLines: 3,
            ),
          ],
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context), child: const Text('Cancel')),
          FilledButton(
            onPressed: () async {
              try {
                await ref.read(gitRepositoryProvider).commit(repoId, messageController.text);
                ref.refresh(commitLogProvider(repoId)); // Refresh log
                if (context.mounted) Navigator.pop(context);
                if (context.mounted) ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Commit successful')));
              } catch (e) {
                 if (context.mounted) ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Error: $e')));
              }
            },
            child: const Text('Commit'),
  }
}

class _GitHistoryPanel extends ConsumerWidget {
  final String repoId;
  const _GitHistoryPanel({required this.repoId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final commitLogAsync = ref.watch(commitLogProvider(repoId));

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Padding(
          padding: const EdgeInsets.all(8.0),
          child: Text('HISTORY', style: Theme.of(context).textTheme.labelSmall),
        ),
        Expanded(
          child: commitLogAsync.when(
            data: (commits) => ListView.builder(
              itemCount: commits.length,
              itemBuilder: (context, index) {
                final commit = commits[index];
                return ListTile(
                  dense: true,
                  leading: CircleAvatar(
                    radius: 12,
                    backgroundColor: Theme.of(context).colorScheme.secondary.withOpacity(0.2),
                    child: Text(commit.authorName[0].toUpperCase(), style: const TextStyle(fontSize: 10)),
                  ),
                  title: Text(commit.message, maxLines: 1, overflow: TextOverflow.ellipsis),
                  subtitle: Text(commit.shortHash, style: const TextStyle(fontSize: 10)),
                  onTap: () {
                    showDialog(
                      context: context,
                      builder: (context) => AlertDialog(
                        title: const Text('Checkout Commit?'),
                        content: Text('Do you want to checkout ${commit.shortHash}? \nWARNING: This will detach HEAD.'),
                        actions: [
                          TextButton(onPressed: () => Navigator.pop(context), child: const Text('Cancel')),
                          TextButton(
                            onPressed: () async {
                               try {
                                  await ref.read(gitRepositoryProvider).checkout(repoId, commit.hash);
                                  ref.refresh(fileTreeProvider(repoId)); // Refresh file tree
                                  ref.refresh(commitLogProvider(repoId)); // Refresh log (HEAD moved)
                                  if (context.mounted) Navigator.pop(context);
                               } catch(e) {
                                  if (context.mounted) ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Error: $e')));
                               }
                            }, 
                            child: const Text('Checkout')
                          ),
                        ],
                      ),
                    );
                  },
                );
              },
            ),
            loading: () => const Center(child: CircularProgressIndicator()),
            error: (err, stack) => Center(child: Text('Error: $err')),
          ),
        ),
      ],
    );
  }
}
