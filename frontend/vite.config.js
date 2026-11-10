import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 4200
  },
  resolve: {
    alias: {
      react: path.resolve(import.meta.dirname, './node_modules/react'),
      'react-dom': path.resolve(import.meta.dirname, './node_modules/react-dom')
    }
  }
});