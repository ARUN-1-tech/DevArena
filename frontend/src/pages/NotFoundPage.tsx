import React from 'react';
import { Link } from 'react-router-dom';
import { MotionContainer } from '../components/animation/MotionContainer';
import { Button } from '../components/ui/Button';
import { AlertTriangle, Home } from 'lucide-react';

export const NotFoundPage: React.FC = () => {
  return (
    <MotionContainer className="text-center py-20 space-y-6 max-w-md mx-auto">
      <div className="w-16 h-16 rounded-2xl bg-amber-100 text-amber-600 flex items-center justify-center mx-auto">
        <AlertTriangle className="w-8 h-8" />
      </div>
      <h1 className="text-3xl font-bold text-slate-900">404 - Arena Zone Not Found</h1>
      <p className="text-sm text-slate-600 font-mono">
        The route you are navigating to has not yet been unlocked or does not exist.
      </p>
      <Link to="/">
        <Button variant="primary" leftIcon={<Home className="w-4 h-4" />}>
          Return to Arena Lobby
        </Button>
      </Link>
    </MotionContainer>
  );
};
