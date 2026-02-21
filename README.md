# Glossaar

Readmes can be found in frontend and backend directories

## infra setup

### frontend

Build the files locally:
`cd frontent && npm run dev`

Move files to server:
`scp -P 22 -r build ubuntu@193.40.157.84:~/temp-build`

Move to the correct directory(this path is set in the nginx config):
`sudo mv temp-build/* /var/www/glossaar/*`

### nginx

Move files to server
`scp -P 22 -r infra/nginx/*  ubuntu@193.40.157.84:~/temp-nginx`

On the server, move the files to the correct directory:
`sudo rm -rf /etc/nginx/sites-enabled &&  sudo mv temp-nginx/* /etc/nginx/`

Tell nginx to reload the config:
`sudo nginx -s reload`
