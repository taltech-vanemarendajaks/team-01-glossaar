# Glossaar

## Devloping on a local machine

### Frontend

1. Easy way - just open /frontend directory in an editor that supports [devcontainers spec](https://containers.dev/).
2. A bit harder alternative way - If the first option doesn't work, install the runtime and deps manually:
    - Install current node LTS v24(npm v11 comes bundeled). Probably will work with earlier versions as §well, but it is not tested.
    - run `npm install` to install projects dependencies
    - run `npm dev` to start local development server

Which ever one you choose, you'll have the frontend dev server running at localhost:5173.