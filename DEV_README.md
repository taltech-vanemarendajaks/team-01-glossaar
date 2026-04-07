# Development with Docker Compose

This guide is based on `docker-compose.yml` in the project root.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Services](#services)
- [Start the stack](#start-the-stack)
- [Run and use](#run-and-use)
- [Logs (FR and BE)](#logs-fr-and-be)
- [Swagger / OpenAPI](#swagger--openapi)
- [Common commands](#common-commands)

## Prerequisites

- Docker Desktop or Docker Engine + Docker Compose v2
- `.env` file in project root (you can copy from `.env.example`)

## Services

- `db`: Postgres 18
- `backend`: Spring Boot app (runs in dev profile; reloads via `docker compose watch`)
- `frontend`: Vite dev server runnig on default Vite devserver port 5173

## Database migrations

Modifications to the database are done via migrations. A modification can be an alteration to a single column or creating multiple related tables(e.g. user and user_group, group table should be separate). New migrations should be made in `backend/src/main/resources/db/migrations` and these will be applied when the application starts. The migrations should also be compatible to run on H2 in-memory DB, that our tests use.
To preserve the sequence how migrations are applied, the file name should start with the migration sequence number e.g. `002__migration_name`.

This is not encouraged but if there is a wish to not write the migration manually:

1. set application-dev.properties spring.jpa.hibernate.ddl-auto to `update` and do your changes in the entities.
2. Extract the schema via pgdump `docker exec -it <container-hash> pg_dump -U ${POSTGRES_USER} -d ${POSTGRES_DB} --schema-only --table <table_name> > dump.sql`
2. Remove all extra information and just keep what is relevant for the changes you made.
3. Make liquibase migration file.

Connecting to database on the cli. db is a container name, hash can also be used as in the previous example.
`docker exec -it db psql -U ${POSTGRES_USER} -d ${POSTGRES_DB}`

## Start and run

From the project root:

```bash
docker compose up -d
```

To build/pull and start cleanly:

```bash
docker compose up -d --build
```

To run attached with watch mode in one command:

```bash
docker compose up --watch
```

This keeps logs in the current terminal and stops when you exit (`Ctrl+C`).

For detached mode with watch, use two terminals:

```bash
docker compose up -d --build
docker compose watch backend
```

Check service status:

```bash
docker compose ps
```

- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080`
- Database: `localhost:5432`.

Because `frontend/` is mounted as a volume, Vite updates live when frontend files change.

This syncs `backend/src` and `backend/pom.xml` into the container and restarts backend when needed.

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

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`


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
