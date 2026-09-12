import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { Swords, Mail, Lock, AlertCircle } from 'lucide-react';
import { motion } from 'framer-motion';

export const LoginPage: React.FC = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const from = (location.state as { from?: { pathname: string } })?.from?.pathname || '/home';

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (!email.trim() || !password) {
      setError('Please enter both email and password.');
      return;
    }

    try {
      setIsLoading(true);
      await login({ email: email.trim(), password });
      navigate(from, { replace: true });
    } catch (err: unknown) {
      const msg =
        err && typeof err === 'object' && 'message' in err
          ? (err as { message: string }).message
          : 'Invalid email or password. Please try again.';
      setError(msg);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-[85vh] flex items-center justify-center px-4 py-12">
      <motion.div
        initial={{ opacity: 0, y: 15 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.4 }}
        className="max-w-md w-full"
      >
        <Card className="p-8 sm:p-10 bg-sandwich-900/90 border-sandwich-700/80 shadow-luxury-card rounded-2xl backdrop-blur-md">
          {/* Header */}
          <div className="text-center space-y-3 mb-8">
            <div className="w-14 h-14 rounded-2xl bg-sandwich-800 border border-sandwich-600 text-sandwich-50 flex items-center justify-center mx-auto shadow-glow-silver">
              <Swords className="w-7 h-7 text-sandwich-100" />
            </div>
            <div>
              <h2 className="text-2xl font-extrabold tracking-tight text-sandwich-50">
                WELCOME BACK, PLAYER
              </h2>
              <p className="text-sm text-sandwich-300 mt-1">
                Enter your credentials to step back into the battle arena.
              </p>
            </div>
          </div>

          {/* Error notice */}
          {error && (
            <div className="mb-6 p-3.5 rounded-xl bg-red-950/40 border border-red-800/80 flex items-center gap-2.5 text-xs text-red-200 font-medium">
              <AlertCircle className="w-4 h-4 shrink-0 text-red-400" />
              <span>{error}</span>
            </div>
          )}

          {/* Form */}
          <form onSubmit={handleSubmit} className="space-y-4">
            <Input
              label="Email or Username"
              type="text"
              placeholder="player@devarena.io"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              leftIcon={<Mail className="w-4 h-4 text-sandwich-400" />}
              autoComplete="username"
              required
            />

            <Input
              label="Password"
              type="password"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              leftIcon={<Lock className="w-4 h-4 text-sandwich-400" />}
              autoComplete="current-password"
              required
            />

            <div className="flex items-center justify-end text-xs font-medium text-sandwich-400">
              <button
                type="button"
                onClick={() => alert('Password reset will be available soon.')}
                className="hover:text-sandwich-200 transition-colors"
              >
                Forgot password?
              </button>
            </div>

            <Button
              type="submit"
              variant="primary"
              size="lg"
              className="w-full mt-2"
              isLoading={isLoading}
              leftIcon={<Swords className="w-4 h-4 text-sandwich-950" />}
            >
              ENTER THE ARENA
            </Button>
          </form>

          {/* Switch to Register */}
          <div className="mt-8 pt-6 border-t border-sandwich-800 text-center text-xs text-sandwich-400 font-mono">
            <span>New challenger? </span>
            <Link to="/register" className="font-bold text-sandwich-100 hover:text-white underline underline-offset-2">
              Create an account
            </Link>
          </div>
        </Card>
      </motion.div>
    </div>
  );
};
