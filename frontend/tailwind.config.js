/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        // "Serious Shadow Sandwich" Luxury Monochromatic Palette
        sandwich: {
          50: '#FFFFFF',  // Crisp pure white (Primary headers, active highlights)
          100: '#F0F1F3', // Soft misty white (Body primary, card titles)
          200: '#D4D7DC', // Pale cool silver (Body secondary, borders)
          300: '#B2B6BD', // Muted silver ash (Subtitles, active metadata)
          400: '#868A94', // Mid slate graphite (Inactive icons, secondary labels)
          500: '#5C6069', // Deep charcoal mist (Dividers, subtle lines)
          600: '#3E4148', // Dark graphite (Card border highlights, active hover)
          700: '#27292F', // Deep slate onyx (Input fields, surface wells)
          800: '#1A1B1F', // Dark carbon (Card backgrounds, floating panels)
          900: '#111215', // Midnight obsidian (Sidebars, headers, modal layer)
          950: '#0B0C0E', // Deepest pitch void (Canvas root background)
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
        'luxury': '0 10px 40px -10px rgba(0, 0, 0, 0.7)',
        'luxury-card': '0 4px 20px -2px rgba(0, 0, 0, 0.5), inset 0 1px 0 0 rgba(255, 255, 255, 0.05)',
        'glow-white': '0 0 25px -5px rgba(255, 255, 255, 0.2)',
        'glow-silver': '0 0 20px -5px rgba(212, 215, 220, 0.15)',
        'glow-cyan': '0 0 20px -5px rgba(0, 229, 255, 0.3)',
        'glow-violet': '0 0 20px -5px rgba(124, 58, 237, 0.3)',
        'glow-emerald': '0 0 20px -5px rgba(16, 185, 129, 0.3)',
      },
    },
  },
  plugins: [],
};
