import React from 'react';
import { motion } from 'framer-motion';
import { Button } from '../ui/Button';
import { Card } from '../ui/Card';
import { Swords, Code, ShieldCheck, Zap, Terminal } from 'lucide-react';

export const FinalCtaSection: React.FC = () => {
  return (
    <section className="py-20 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <Card className="relative overflow-hidden p-8 sm:p-14 bg-gradient-to-br from-slate-900 via-slate-900 to-slate-950 text-white border-slate-800 shadow-2xl text-center">
        {/* Ambient neon radial gradients */}
        <div className="absolute -top-24 -left-24 w-80 h-80 bg-cyan-500/20 blur-3xl rounded-full pointer-events-none" />
        <div className="absolute -bottom-24 -right-24 w-80 h-80 bg-violet-500/20 blur-3xl rounded-full pointer-events-none" />

        <div className="relative z-10 max-w-3xl mx-auto space-y-6">
          <motion.div
            initial={{ opacity: 0, y: 15 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.4 }}
            className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-slate-800/80 border border-slate-700 text-cyan-400 text-xs font-mono font-bold uppercase tracking-wider"
          >
            <Terminal className="w-3.5 h-3.5 text-cyan-400" />
            <span>SEASON 1 REGISTRATION OPEN</span>
          </motion.div>

          <h2 className="text-4xl sm:text-5xl font-extrabold tracking-tight text-white leading-tight">
            READY TO ENTER THE ARENA?
          </h2>

          <p className="text-base sm:text-lg text-slate-300 leading-relaxed max-w-xl mx-auto">
            Join thousands of developers competing in real-time right now. Prove your algorithmic
            might, level up your developer DNA, and climb to Grandmaster.
          </p>

          <div className="flex flex-wrap items-center justify-center gap-4 pt-4">
            <Button
              variant="glow"
              size="lg"
              leftIcon={<Swords className="w-5 h-5" />}
              onClick={() => alert('Battle matchmaking will open in Module 06!')}
            >
              START BATTLING NOW
            </Button>

            <Button
              variant="outline"
              size="lg"
              className="border-slate-700 text-white bg-slate-800/70 hover:bg-slate-700"
              leftIcon={<Code className="w-4 h-4 text-cyan-400" />}
              onClick={() => {
                const el = document.getElementById('modes');
                el?.scrollIntoView({ behavior: 'smooth' });
              }}
            >
              EXPLORE CHALLENGES
            </Button>
          </div>

          {/* Feature trust pills */}
          <div className="pt-8 border-t border-slate-800/80 flex flex-wrap items-center justify-center gap-6 text-xs font-mono text-slate-400">
            <span className="flex items-center gap-1.5">
              <Zap className="w-3.5 h-3.5 text-amber-400" />
              15+ Languages Supported
            </span>
            <span className="flex items-center gap-1.5">
              <ShieldCheck className="w-3.5 h-3.5 text-emerald-400" />
              Anti-Cheat Sandboxed Runner
            </span>
            <span className="flex items-center gap-1.5">
              <Swords className="w-3.5 h-3.5 text-cyan-400" />
              Instant Matchmaking (&lt;4s)
            </span>
          </div>
        </div>
      </Card>
    </section>
  );
};
