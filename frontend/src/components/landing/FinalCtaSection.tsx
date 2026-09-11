import React from 'react';
import { motion, useReducedMotion } from 'framer-motion';
import { Button } from '../ui/Button';
import { Card } from '../ui/Card';
import { Swords } from 'lucide-react';

export const FinalCtaSection: React.FC = () => {
  const shouldReduceMotion = useReducedMotion();

  return (
    <section className="py-24 max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
      <Card className="relative overflow-hidden p-10 sm:p-16 bg-slate-900 text-white border-slate-800 shadow-xl text-center rounded-3xl">
        {/* Subtle decorative particles */}
        <motion.div
          animate={shouldReduceMotion ? {} : { y: [0, -10, 0] }}
          transition={{ duration: 4, repeat: Infinity, ease: 'easeInOut' }}
          className="absolute top-8 left-12 w-2 h-2 rounded-full bg-cyan-400 opacity-60"
        />
        <motion.div
          animate={shouldReduceMotion ? {} : { y: [0, 8, 0] }}
          transition={{ duration: 4.5, repeat: Infinity, ease: 'easeInOut', delay: 0.5 }}
          className="absolute bottom-10 right-14 w-3 h-3 rounded-full bg-violet-400 opacity-60"
        />
        <motion.div
          animate={shouldReduceMotion ? {} : { y: [0, -6, 0] }}
          transition={{ duration: 5, repeat: Infinity, ease: 'easeInOut', delay: 1 }}
          className="absolute top-12 right-20 w-1.5 h-1.5 rounded-full bg-emerald-400 opacity-60"
        />

        <div className="relative z-10 max-w-md mx-auto space-y-6">
          <h2 className="text-3xl sm:text-4xl lg:text-5xl font-extrabold tracking-tight text-white leading-tight">
            READY TO ENTER THE ARENA?
          </h2>

          <p className="text-base text-slate-300">
            Your next challenge is waiting.
          </p>

          <div className="pt-2">
            <Button
              variant="glow"
              size="lg"
              leftIcon={<Swords className="w-5 h-5" />}
              onClick={() => {
                const el = document.getElementById('modes');
                el?.scrollIntoView({ behavior: 'smooth' });
              }}
            >
              ⚔️ START BATTLE
            </Button>
          </div>
        </div>
      </Card>
    </section>
  );
};
