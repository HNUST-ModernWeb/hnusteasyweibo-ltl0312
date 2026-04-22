import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'
import { SERVER_PORT } from './src/constants/server.js'

export default defineConfig({
  plugins: [vue()],
  server: {
    allowedHosts: ['5aa32483.r24.cpolar.top'], // 👈 替换为你的实际域名
    port: 3000,
    open: true,
    proxy: {
      '/api': {
        target: `http://localhost:${SERVER_PORT}`,
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '/api/v1')
      },
      '/uploads': {
        target: `http://localhost:${SERVER_PORT}`,
        changeOrigin: true
      }
    }
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  }
})
