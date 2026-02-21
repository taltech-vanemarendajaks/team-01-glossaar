# Glossaar

Readmes can be found in frontend and backend directories

## infra setup

### frontend manual deploy

Build the files locally:
`cd frontend && npm run build`

Move files to server(presumes ssh-agent/config is set up correctly, if not then add -i ~/.ssh/{keyname}):
`scp -P 22 -r build/* ubuntu@193.40.157.84:~/temp-frontend-build`

Move to the correct directory(this path is set in the nginx config):
`sudo rsync -av ~/temp-frontend-build/ /var/www/glossaar/`

### nginx manual deploy

Move files to server(presumes ssh-agent/config is set up correctly, if not then add -i ~/.ssh/{keyname}):
`scp -P 22 -r infra/nginx/* ubuntu@193.40.157.84:~/temp-nginx`

On the server, sync the files to the correct directory:
`sudo rsync -av ~/temp-nginx/ /etc/nginx/`

Tell nginx to test and reload the config:
`sudo nginx -t && sudo nginx -s reload`
