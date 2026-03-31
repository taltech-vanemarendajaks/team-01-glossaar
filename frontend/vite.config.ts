import tailwindcss from '@tailwindcss/vite';
import { defineConfig } from 'vitest/config';
import { playwright } from '@vitest/browser-playwright';
import { sveltekit } from '@sveltejs/kit/vite';
import path from 'path';

export default defineConfig(() => {
  const runningWithinDocker = !!process.env.DOCKER_ENV;
  const backendUrl = runningWithinDocker
    ? // To make oauth work both within and outside of docker,
      // we'll need to target host machine localhost instead of container localhost when running in docker
      'http://host.docker.internal:8080'
    : 'http://localhost:8080';

  return {
    plugins: [tailwindcss(), sveltekit()],

    server: {
      host: true,
      port: 5173,
      strictPort: true,
      proxy: {
        '/api': backendUrl,
        '/login/oauth2': backendUrl,
        '/login': {
          target: backendUrl,
          rewrite: (path) => path.replace(/^\/login/, '/oauth2/authorization/github')
        }
      }
    },

    resolve: {
      alias: {
        $lib: path.resolve('./src/lib')
      }
    },

    test: {
      expect: { requireAssertions: true },
      projects: [
        {
          extends: './vite.config.ts',
          test: {
            name: 'client',
            browser: {
              enabled: true,
              provider: playwright(),
              instances: [{ browser: 'chromium', headless: true }]
            },
            include: ['src/**/*.svelte.{test,spec}.{js,ts}'],
            exclude: ['src/lib/server/**']
          }
        },
        {
          extends: './vite.config.ts',
          test: {
            name: 'server',
            environment: 'node',
            include: ['src/**/*.{test,spec}.{js,ts}'],
            exclude: ['src/**/*.svelte.{test,spec}.{js,ts}']
          }
        }
      ]
    }
  };
});
