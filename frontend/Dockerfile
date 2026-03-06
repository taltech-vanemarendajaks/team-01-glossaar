FROM node:lts-alpine AS build-step

WORKDIR /app

COPY package.json package-lock.json ./

RUN npm ci --verbose

COPY . .

RUN npm run build


FROM nginx:stable-alpine

# target is default nginx html directory
COPY --from=build-step /app/build /usr/share/nginx/html

# SPA fallback: serve index.html for all unknown routes
RUN rm /etc/nginx/conf.d/default.conf
RUN cat <<'EOF' > /etc/nginx/conf.d/default.conf
server {
    listen 80;
    server_name _;
    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }
}
EOF

# label necessary so the image on ghcr.io gets linked to the repository on github.com when pushing locally
LABEL org.opencontainers.image.source=https://github.com/taltech-vanemarendajaks/team-01-glossaar

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]