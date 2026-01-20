import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:frontend/features/dashboard/dashboard_page.dart';
import 'package:frontend/features/workspace/workspace_page.dart';

final router = GoRouter(
  initialLocation: '/',
  routes: [
    GoRoute(
      path: '/',
      builder: (context, state) => const DashboardPage(),
    ),
    GoRoute(
      path: '/workspace/:repoId',
      builder: (context, state) {
        final repoId = state.pathParameters['repoId']!;
        return WorkspacePage(repoId: repoId);
      },
    ),
  ],
);
