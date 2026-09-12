import React from 'react';
import { Outlet } from 'react-router-dom';
import { Navbar } from '../components/layout/Navbar';
import { Footer } from '../components/layout/Footer';
import { useSystemHealth } from '../hooks/useSystemHealth';

export const RootLayout: React.FC = () => {
  const { isConnected } = useSystemHealth();

  return (
    <div className="min-h-screen flex flex-col bg-[#0D0506] text-[#EFEFE1] selection:bg-[#72282D] selection:text-white">
      <Navbar backendConnected={isConnected} />
      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <Outlet />
      </main>
      <Footer />
    </div>
  );
};
