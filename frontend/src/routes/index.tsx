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
import { LeaderboardPage } from '../pages/app/LeaderboardPage';
import { SkillsPage } from '../pages/app/SkillsPage';
import { AchievementsPage } from '../pages/app/AchievementsPage';
import { FriendsPage } from '../pages/app/FriendsPage';
import { ProfilePage } from '../pages/app/ProfilePage';

// Route Guards
import { ProtectedRoute } from './ProtectedRoute';
import { PublicAuthRoute } from './PublicAuthRoute';

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
        path: 'leaderboard',
        element: <LeaderboardPage />,
      },
      {
        path: 'skills',
        element: <SkillsPage />,
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
        path: 'profile',
        element: <ProfilePage />,
      },
    ],
  },

  // Fallback 404
  {
    path: '*',
    element: <NotFoundPage />,
  },
]);
