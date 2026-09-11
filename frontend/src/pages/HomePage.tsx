import React from 'react';
import { HeroSection } from '../components/landing/HeroSection';
import { LiveArenaSection } from '../components/landing/LiveArenaSection';
import { GameModesSection } from '../components/landing/GameModesSection';
import { ProgressionSection } from '../components/landing/ProgressionSection';
import { SkillTreeSection } from '../components/landing/SkillTreeSection';
import { LeaderboardSection } from '../components/landing/LeaderboardSection';
import { AchievementsSection } from '../components/landing/AchievementsSection';
import { FinalCtaSection } from '../components/landing/FinalCtaSection';

export const HomePage: React.FC = () => {
  return (
    <div className="space-y-4">
      {/* 01. HERO */}
      <HeroSection />

      {/* 02. LIVE ARENA */}
      <LiveArenaSection />

      {/* 03. GAME MODES */}
      <GameModesSection />

      {/* 04. DEVELOPER PROGRESSION */}
      <ProgressionSection />

      {/* 05. SKILL TREE */}
      <SkillTreeSection />

      {/* 06. LEADERBOARD */}
      <LeaderboardSection />

      {/* 07. ACHIEVEMENTS */}
      <AchievementsSection />

      {/* 08. FINAL CTA */}
      <FinalCtaSection />
    </div>
  );
};
