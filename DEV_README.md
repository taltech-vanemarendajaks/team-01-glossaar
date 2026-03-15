# Development with Docker Compose

This guide is based on `docker-compose.yml` in the project root.

## Prerequisites

- Docker Desktop or Docker Engine + Docker Compose v2
- `.env` file in project root (you can copy from `.env.example`)

## Services

- `db`: Postgres 16 (`5432`)
- `backend`: Spring Boot app (runs in dev profile, restarts on file changes)
- `frontend`: Vite dev server (`${FRONTEND_DEV_PORT:-5173}`)

## Start the stack

From the project root:

```bash
docker compose up -d
```

To build/pull and start cleanly:

```bash
docker compose up -d --build
```

Check service status:

```bash
docker compose ps
```

## Run and use

- Frontend: `http://localhost:5173` (or your `FRONTEND_DEV_PORT` from `.env`)
- Backend is consumed by frontend inside Docker network (`backend` service name).
- Database is available on host `localhost:5432`.

Because source folders are mounted as volumes:

- Editing files in `frontend/` updates the Vite app live.
- Editing files in `backend/src/main/java`, `backend/src/main/resources`, or `backend/pom.xml` triggers backend restart.

## Logs (FR and BE)

Show recent logs:

```bash
docker compose logs --tail=200 frontend
docker compose logs --tail=200 backend
```

Follow live logs:

```bash
docker compose logs -f frontend
docker compose logs -f backend
```

Follow both at once:

```bash
docker compose logs -f frontend backend
```

## Swagger / OpenAPI

After backend is running, open:

- Swagger UI: `http://localhost:8083/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8083/v3/api-docs`

If you only exposed frontend port in Docker, add backend port mapping in `docker-compose.yml`:

```yaml
backend:
  ports:
    - "${BACKEND_PORT:-8083}:8080"
```

## Common commands

Stop services:

```bash
docker compose stop
```

Stop and remove containers/network:

```bash
docker compose down
```

Stop and also remove volumes (deletes DB data and cached dependencies):

```bash
docker compose down -v
```

Restart a single service:

```bash
docker compose restart frontend
docker compose restart backend
```
