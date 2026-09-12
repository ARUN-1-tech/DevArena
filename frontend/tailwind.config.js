/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        // "LUXE" Palette - Burgundy (#72282D), Taupe (#AC9C8D), Sandstone (#D1C7BD), Silver (#D9D9D9), Ivory (#EFEFE1)
        luxe: {
          wine: '#72282D',
          taupe: '#AC9C8D',
          sandstone: '#D1C7BD',
          silver: '#D9D9D9',
          ivory: '#EFEFE1',
        },
        sandwich: {
          50: '#FFFFFF',  // Crisp pure white (Highlights, peak white)
          100: '#EFEFE1', // Luxe Silk Ivory (Swatch 5 - primary headings, active tabs)
          200: '#D9D9D9', // Luxe Pale Silver (Swatch 4 - secondary typography, borders)
          300: '#D1C7BD', // Luxe Sandstone (Swatch 3 - warm metadata, subtitles)
          400: '#AC9C8D', // Luxe Warm Taupe (Swatch 2 - muted text, inactive icons)
          500: '#72282D', // Luxe Signature Burgundy (Swatch 1 - brand accents, badges)
          600: '#5A1E22', // Deep Velvet Wine (Active borders, hover states)
          700: '#3A1417', // Deep Wine Onyx (Inputs, wells, progress tracks)
          800: '#220D0F', // Dark Velvet Carbon (Cards, panels, modals)
          900: '#160809', // Midnight Obsidian (Navbars, fixed headers, table headers)
          950: '#0D0506', // Deepest Velvet Noir Void (Canvas root background)
        },
        arena: {
          50: '#f0fdfa',
          100: '#ccfbf1',
          200: '#99f6e4',
          300: '#5eead4',
          400: '#2dd4bf',
          500: '#0d9488',
          600: '#0f766e',
          700: '#115e59',
          800: '#134e4a',
          900: '#134e4a',
        },
        cyber: {
          cyan: '#00e5ff',
          blue: '#0284c7',
          violet: '#7c3aed',
          amber: '#f59e0b',
          emerald: '#10b981',
          rose: '#f43f5e',
        },
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', '-apple-system', 'sans-serif'],
        mono: ['JetBrains Mono', 'Fira Code', 'monospace'],
      },
      boxShadow: {
        'luxury': '0 10px 40px -10px rgba(0, 0, 0, 0.85)',
        'luxury-card': '0 4px 20px -2px rgba(0, 0, 0, 0.7), inset 0 1px 0 0 rgba(209, 199, 189, 0.08)',
        'glow-wine': '0 0 25px -5px rgba(114, 40, 45, 0.55)',
        'glow-taupe': '0 0 20px -5px rgba(172, 156, 141, 0.35)',
        'glow-ivory': '0 0 20px -5px rgba(239, 239, 225, 0.25)',
        'glow-silver': '0 0 20px -5px rgba(217, 217, 217, 0.2)',
        'glow-white': '0 0 25px -5px rgba(255, 255, 255, 0.2)',
        'glow-cyan': '0 0 20px -5px rgba(0, 229, 255, 0.3)',
        'glow-violet': '0 0 20px -5px rgba(124, 58, 237, 0.3)',
        'glow-emerald': '0 0 20px -5px rgba(16, 185, 129, 0.3)',
      },
    },
  },
  plugins: [],
};
