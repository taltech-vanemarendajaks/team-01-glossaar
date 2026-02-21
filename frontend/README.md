# Glossaar

## Devloping on a local machine

### Frontend

Install the runtime and deps manually:
    - Install some modern node.js LTS version (nvm or other version manager recommended)
    - run `npm install` to install projects dependencies
    - run `npm run dev` to start local development server

Which ever one you choose, you'll have the frontend dev server running at [localhost:5173](http://localhost:5173/).

## Framework

Frontend uses [shadcn](https://shadcn-svelte.com/docs/components) and [tailwind](https://tailwindcss.com/)

## Building

To create a production version of your app:

```sh
npm run build
```

You can preview the production build with `npm run preview`.

> To deploy your app, you may need to install an [adapter](https://svelte.dev/docs/kit/adapters) for your target environment.

## misc

To recreate this project with the same configuration:

```sh
# recreate this project
npx sv create --template minimal --types ts --add prettier eslint sveltekit-adapter="adapter:static" vitest="usages:unit,component" --install npm frontend
```
