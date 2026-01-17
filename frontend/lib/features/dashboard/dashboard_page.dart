import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:frontend/data/repositories/git_repository.dart';
import 'package:frontend/features/dashboard/dashboard_provider.dart';

class DashboardPage extends ConsumerWidget {
  const DashboardPage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final reposAsync = ref.watch(dashboardRepositoriesProvider);

    return Scaffold(
      appBar: AppBar(
        title: const Text('GitDense'),
        actions: [
          IconButton(
            icon: const Icon(Icons.add),
            tooltip: 'Add Repository',
            onPressed: () => _addRepository(context, ref),
          ),
          const SizedBox(width: 16),
        ],
      ),
      body: reposAsync.when(
        data: (repos) {
          if (repos.isEmpty) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                   Icon(Icons.code, size: 64, color: Theme.of(context).colorScheme.primary),
                   const SizedBox(height: 16),
                   Text('No Repositories Found', style: Theme.of(context).textTheme.headlineSmall),
                   const SizedBox(height: 24),
                   ElevatedButton.icon(
                     onPressed: () => _addRepository(context, ref),
                     icon: const Icon(Icons.add),
                     label: const Text('Add Repository'),
                   ),
                ],
              ),
            );
          }
          return GridView.builder(
            padding: const EdgeInsets.all(16),
            gridDelegate: const SliverGridDelegateWithMaxCrossAxisExtent(
              maxCrossAxisExtent: 300,
              childAspectRatio: 1.2,
              crossAxisSpacing: 16,
              mainAxisSpacing: 16,
            ),
            itemCount: repos.length,
            itemBuilder: (context, index) {
              final repo = repos[index];
              return Card(
                clipBehavior: Clip.antiAlias,
                child: InkWell(
                  onTap: () {
                    GoRouter.of(context).go('/workspace/${repo.id}');
                  },
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Row(
                          children: [
                            const Icon(Icons.folder_open, size: 20),
                            const SizedBox(width: 8),
                            Expanded(
                              child: Text(
                                repo.name,
                                style: Theme.of(context).textTheme.titleMedium,
                                overflow: TextOverflow.ellipsis,
                              ),
                            ),
                          ],
                        ),
                        const Spacer(),
                        Text(
                          repo.localPath,
                          style: Theme.of(context).textTheme.bodySmall,
                          overflow: TextOverflow.ellipsis,
                          maxLines: 2,
                        ),
                        const SizedBox(height: 8),
                         Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            const Text('Last analyzed:'),
                             Text(
                              repo.lastAnalyzed != null ? '${repo.lastAnalyzed!.day}/${repo.lastAnalyzed!.month}' : 'Never',
                               style: Theme.of(context).textTheme.bodySmall?.copyWith(color: Colors.white70),
                             ),
                          ],
                        )
                      ],
                    ),
                  ),
                ),
              );
            },
          );
        },
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (err, stack) => Center(child: Text('Error: $err')),
      ),
    );
  }

  Future<void> _addRepository(BuildContext context, WidgetRef ref) async {
    String? selectedDirectory = await FilePicker.platform.getDirectoryPath();

    if (selectedDirectory != null) {
      if (context.mounted) {
        final nameController = TextEditingController(text: selectedDirectory.split('\\').last);
        
        await showDialog(
          context: context,
          builder: (context) => AlertDialog(
            title: const Text('Add Repository'),
            content: TextField(
              controller: nameController,
              decoration: const InputDecoration(labelText: 'Repository Name'),
              autofocus: true,
            ),
            actions: [
              TextButton(onPressed: () => Navigator.pop(context), child: const Text('Cancel')),
              FilledButton(
                onPressed: () async {
                  try {
                    await ref.read(gitRepositoryProvider).addRepository(selectedDirectory, nameController.text);
                    ref.refresh(dashboardRepositoriesProvider); // Reload list
                    if (context.mounted) Navigator.pop(context);
                  } catch (e) {
                    if (context.mounted) {
                       ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Error: $e')));
                    }
                  }
                },
                child: const Text('Add'),
              ),
            ],
          ),
        );
      }
    }
  }
}
