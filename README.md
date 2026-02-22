# Glossaar

Readmes can be found in frontend and backend directories

## infra setup

## Docker

### server

Currently the server has docker running and serving the traffic via nginx container. The only other container we have currently is the `frontend` container which it servers via glossaar.href.ee. There is also `watchtower` container that handles pulling and instanciating the containers automatically. No manual deployments should be necessary.

### frontend GHCR deploy

Should be done in the repository via GitHub Action 'Build and publish Docker image'

#### can be done locally as well

Commands presume you are in the `frontend` directory and that you have authenticated with your GitHub PAT(Personal Access Token).
`echo <GH_PAT> | docker login ghcr.io -u <GH_USERNAME> --password-stdin`

Build the frontend image(glossaar-frontend:latest tag is added automatically):
`docker build --platform linux/amd64 --tag glossaar-frontend .`

Tag the image with artifactory - necessary for pushing to GHCR
`docker tag glossaar-frontend:latest ghcr.io/taltech-vanemarendajaks/team-01-glossaar-frontend:latest`

Push image to GHCR
`docker push ghcr.io/taltech-vanemarendajaks/team-01-glossaar-frontend:latest`

Then the image can be found at ghcr.io/taltech-vanemarendajaks/team-01-glossaar-frontend

The image also should pop up at out repo page.

### frontend manual deploy (not needed currently, documented only for emergencies)

Commands presume you are in the `frontend` directory.

Build the files locally:
`npm run build`

Move files to server(presumes ssh-agent/config is set up correctly, if not then add -i ~/.ssh/{keyname}):
`scp -P 22 -r build/* ubuntu@193.40.157.84:~/temp-frontend-build`

Move to the correct directory(this path is set in the nginx config):
`sudo rsync -av ~/temp-frontend-build/ /var/www/glossaar/`

### nginx manual deploy (not needed currently, documented only for emergencies)

Commands presume you are in the `infra` directory.

Move files to server(presumes ssh-agent/config is set up correctly, if not then add -i ~/.ssh/{keyname}):
`scp -P 22 -r nginx/* ubuntu@193.40.157.84:~/temp-nginx`

On the server, sync the files to the correct directory:
`sudo rsync -av ~/temp-nginx/ /etc/nginx/`

Tell nginx to test and reload the config:
`sudo nginx -t && sudo nginx -s reload`
