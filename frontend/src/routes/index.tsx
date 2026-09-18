import { createBrowserRouter } from 'react-router-dom';
import { RootLayout } from '../layouts/RootLayout';
import { AppLayout } from '../layouts/AppLayout';
import { HomePage } from '../pages/HomePage';
import { LoginPage } from '../pages/LoginPage';
import { RegisterPage } from '../pages/RegisterPage';
import { PlayerCreationPage } from '../pages/PlayerCreationPage';
import { WelcomePage } from '../pages/WelcomePage';
import { ArenaHomePage } from '../pages/ArenaHomePage';
import { NotFoundPage } from '../pages/NotFoundPage';

import { ArenaPage } from '../pages/app/ArenaPage';
import { ChallengesPage } from '../pages/app/ChallengesPage';
import { ChallengeDetailPage } from '../pages/app/ChallengeDetailPage';
import { CodeLabPage } from '../pages/app/CodeLabPage';
import { LeaderboardPage } from '../pages/app/LeaderboardPage';
import { AchievementsPage } from '../pages/app/AchievementsPage';
import { FriendsPage } from '../pages/app/FriendsPage';
import { ProfilePage } from '../pages/app/ProfilePage';
import { TeamsPage } from '../pages/app/TeamsPage';
import { PublicProfilePage } from '../pages/app/PublicProfilePage';
import { MatchmakingPage } from '../pages/app/MatchmakingPage';
import { BattlePage } from '../pages/app/BattlePage';
import { BattleResultPage } from '../pages/app/BattleResultPage';

// Route Guards
import { ProtectedRoute } from './ProtectedRoute';
import { PublicAuthRoute } from './PublicAuthRoute';
import { AdminRoute } from './AdminRoute';
import { AdminLayout } from '../layouts/AdminLayout';
import { AdminDashboardPage } from '../pages/admin/AdminDashboardPage';
import { AdminPlayersPage } from '../pages/admin/AdminPlayersPage';
import { AdminChallengesPage } from '../pages/admin/AdminChallengesPage';
import { AdminReportsPage } from '../pages/admin/AdminReportsPage';
import { AdminIntegrityPage } from '../pages/admin/AdminIntegrityPage';
import { AdminAuditPage } from '../pages/admin/AdminAuditPage';

export const router = createBrowserRouter([
  // Public Marketing Landing Layout
  {
    path: '/',
    element: <RootLayout />,
    children: [
      {
        index: true,
        element: <HomePage />,
      },
      {
        path: 'login',
        element: (
          <PublicAuthRoute>
            <LoginPage />
          </PublicAuthRoute>
        ),
      },
      {
        path: 'register',
        element: (
          <PublicAuthRoute>
            <RegisterPage />
          </PublicAuthRoute>
        ),
      },
    ],
  },

  // Protected Player Onboarding Experience (No heavy app sidebar)
  {
    path: '/player/create',
    element: (
      <ProtectedRoute>
        <PlayerCreationPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/welcome',
    element: (
      <ProtectedRoute>
        <WelcomePage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/matchmaking',
    element: (
      <ProtectedRoute>
        <MatchmakingPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/battle/:id',
    element: (
      <ProtectedRoute>
        <BattlePage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/battle/:id/result',
    element: (
      <ProtectedRoute>
        <BattleResultPage />
      </ProtectedRoute>
    ),
  },

  // Protected Authenticated Game Application Shell (AppLayout)
  {
    path: '/',
    element: (
      <ProtectedRoute>
        <AppLayout />
      </ProtectedRoute>
    ),
    children: [
      {
        path: 'home',
        element: <ArenaHomePage />,
      },
      {
        path: 'arena',
        element: <ArenaPage />,
      },
      {
        path: 'challenges',
        element: <ChallengesPage />,
      },
      {
        path: 'challenges/:id',
        element: <ChallengeDetailPage />,
      },
      {
        path: 'challenges/:id/solve',
        element: <CodeLabPage />,
      },
      {
        path: 'leaderboard',
        element: <LeaderboardPage />,
      },
      {
        path: 'achievements',
        element: <AchievementsPage />,
      },
      {
        path: 'friends',
        element: <FriendsPage />,
      },
      {
        path: 'teams',
        element: <TeamsPage />,
      },
      {
        path: 'players/:username',
        element: <PublicProfilePage />,
      },
      {
        path: 'profile',
        element: <ProfilePage />,
      },
    ],
  },

  // Protected Admin Portal (AdminRoute + AdminLayout)
  {
    path: '/admin',
    element: (
      <AdminRoute>
        <AdminLayout />
      </AdminRoute>
    ),
    children: [
      {
        index: true,
        element: <AdminDashboardPage />,
      },
      {
        path: 'players',
        element: <AdminPlayersPage />,
      },
      {
        path: 'challenges',
        element: <AdminChallengesPage />,
      },
      {
        path: 'reports',
        element: <AdminReportsPage />,
      },
      {
        path: 'integrity',
        element: <AdminIntegrityPage />,
      },
      {
        path: 'audit',
        element: <AdminAuditPage />,
      },
    ],
  },

  // Fallback 404
  {
    path: '*',
    element: <NotFoundPage />,
  },
]);
