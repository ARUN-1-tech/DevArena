import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { Swords, Mail, Lock, User, Sparkles, AlertCircle } from 'lucide-react';
import { motion } from 'framer-motion';

export const RegisterPage: React.FC = () => {
  const { register } = useAuth();
  const navigate = useNavigate();

  const [email, setEmail] = useState('');
  const [username, setUsername] = useState('');
  const [displayName, setDisplayName] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    // Client-side validation
    if (!email.trim() || !username.trim() || !displayName.trim() || !password) {
      setError('Please fill in all required fields.');
      return;
    }

    if (username.length < 3 || username.length > 30) {
      setError('Username must be between 3 and 30 characters.');
      return;
    }

    if (password.length < 6) {
      setError('Password must be at least 6 characters.');
      return;
    }

    if (password !== confirmPassword) {
      setError('Passwords do not match. Please double-check.');
      return;
    }

    try {
      setIsLoading(true);
      await register({
        email: email.trim().toLowerCase(),
        username: username.trim(),
        displayName: displayName.trim(),
        password,
      });
      // After registration, move directly to Player Creation step
      navigate('/player/create');
    } catch (err: unknown) {
      const msg =
        err && typeof err === 'object' && 'message' in err
          ? (err as { message: string }).message
          : 'Registration failed. Please check your details.';
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
          <div className="text-center space-y-3 mb-6">
            <div className="w-14 h-14 rounded-2xl bg-sandwich-800 border border-sandwich-600 text-sandwich-50 flex items-center justify-center mx-auto shadow-glow-silver">
              <Sparkles className="w-7 h-7 text-sandwich-100" />
            </div>
            <div>
              <h2 className="text-2xl font-extrabold tracking-tight text-sandwich-50">
                JOIN THE ARENA
              </h2>
              <p className="text-sm text-sandwich-300 mt-1">
                Create your player account and begin your competitive coding journey.
              </p>
            </div>
          </div>

          {/* Error notice */}
          {error && (
            <div className="mb-5 p-3.5 rounded-xl bg-red-950/40 border border-red-800/80 flex items-center gap-2.5 text-xs text-red-200 font-medium">
              <AlertCircle className="w-4 h-4 shrink-0 text-red-400" />
              <span>{error}</span>
            </div>
          )}

          {/* Form */}
          <form onSubmit={handleSubmit} className="space-y-3.5">
            <Input
              label="Email Address"
              type="email"
              placeholder="player@devarena.io"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              leftIcon={<Mail className="w-4 h-4 text-sandwich-400" />}
              autoComplete="email"
              required
            />

            <div className="grid grid-cols-2 gap-3">
              <Input
                label="Username"
                type="text"
                placeholder="CodeHero"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                leftIcon={<User className="w-4 h-4 text-sandwich-400" />}
                autoComplete="username"
                required
              />

              <Input
                label="Display Name"
                type="text"
                placeholder="Alex Mercer"
                value={displayName}
                onChange={(e) => setDisplayName(e.target.value)}
                autoComplete="name"
                required
              />
            </div>

            <Input
              label="Password"
              type="password"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              leftIcon={<Lock className="w-4 h-4 text-sandwich-400" />}
              autoComplete="new-password"
              required
            />

            <Input
              label="Confirm Password"
              type="password"
              placeholder="••••••••"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              leftIcon={<Lock className="w-4 h-4 text-sandwich-400" />}
              autoComplete="new-password"
              required
            />

            <Button
              type="submit"
              variant="primary"
              size="lg"
              className="w-full mt-2"
              isLoading={isLoading}
              leftIcon={<Swords className="w-4 h-4 text-sandwich-950" />}
            >
              CREATE PLAYER ACCOUNT
            </Button>
          </form>

          {/* Switch to Login */}
          <div className="mt-6 pt-5 border-t border-sandwich-800 text-center text-xs text-sandwich-400 font-mono">
            <span>Already have an arena account? </span>
            <Link to="/login" className="font-bold text-sandwich-100 hover:text-white underline underline-offset-2">
              Log in
            </Link>
          </div>
        </Card>
      </motion.div>
    </div>
  );
};
