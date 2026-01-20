import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:frontend/features/workspace/workspace_provider.dart';
import 'package:google_fonts/google_fonts.dart';

class CodeEditorWidget extends ConsumerWidget {
  final String repoId;

  const CodeEditorWidget({super.key, required this.repoId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final selectedPath = ref.watch(selectedFileProvider);

    if (selectedPath == null) {
      return const Center(child: Text('Select a file to edit'));
    }

    final contentAsync = ref.watch(fileContentProvider((repoId: repoId, path: selectedPath)));

    return contentAsync.when(
      data: (content) {
        return Container(
          color: const Color(0xFF1E1E1E), // Editor background
          child: SingleChildScrollView(
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: SelectableText(
                content,
                style: GoogleFonts.firaCode(
                  fontSize: 14,
                  color: const Color(0xFFD4D4D4),
                ),
              ),
            ),
          ),
        );
      },
      loading: () => const Center(child: CircularProgressIndicator()),
      error: (err, stack) => Center(child: Text('Error loading file: $err')),
    );
  }
}
