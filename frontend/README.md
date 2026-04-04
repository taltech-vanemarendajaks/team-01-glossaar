# Glossaar

## Devloping on a local machine

You can either:

1. Install the runtime and deps manually:
   - Install node.js LTS 24 version (nvm or other node version manager recommended)
   - run `npm install` to install projects dependencies
   - run `npm run dev` to start local development server

2. Or just run the dev docker compose.

As a result, the frontend dev server will be running. Concrete port will be printed out to stdout.

## Framework

Frontend uses [shadcn](https://shadcn-svelte.com/docs/components) and [tailwind](https://tailwindcss.com/)

## Building

To create a production version of your app:

```sh
npm run build
```

You can preview the production build with `npm run preview`.

To deploy your app, you may need to install an [adapter](https://svelte.dev/docs/kit/adapters) for your target environment.
