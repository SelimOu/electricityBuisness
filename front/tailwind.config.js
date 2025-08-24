/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./src/**/*.{html,ts}'],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        deep: '#0B0F18',
        electric: '#00D1FF',
        neon: '#0AFF9D',
        violet: '#7B61FF'
      },
      fontFamily: {
        poppins: ['Poppins', 'ui-sans-serif', 'system-ui'],
        inter: ['Inter', 'ui-sans-serif', 'system-ui']
      }
    }
  },
  plugins: []
}

