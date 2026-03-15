# Glossaar

Readmes can be found in frontend and backend directories

## infra setup

### server

Currently the server has docker running and serving the traffic via nginx.


### FE/BE GHCR image deploy

After deploying the image to GHCR registry, you should ssh into the server, navigate to ./team-01-glossaar/infra and pull the new images and create new containers.

#### Deploy to GHCR via GH Action "Build and publish Docker image" (preferred method)

Choose the correct application component and see the magic happening.

#### GHCR deploy can also be done locally

NB! {component} can be either `frontend` or `backend` and should be replaced before running the commands.

Commands presume you are in the {component} directory and that you have authenticated with your GitHub PAT(Personal Access Token).
`shell echo <GH_PAT> | docker login ghcr.io -u <GH_USERNAME> --password-stdin`

Build the {component} image(glossaar-{component}:latest tag is added automatically):
`docker build --platform linux/amd64 --tag glossaar/{component} .`

Tag the image with artifactory - necessary for pushing to GHCR
`docker tag glossaar/{component}:latest ghcr.io/taltech-vanemarendajaks/team-01-glossaar/{component}:latest`

Push image to GHCR
`docker push ghcr.io/taltech-vanemarendajaks/team-01-glossaar/{component}:latest`

Then the image can be found at ghcr.io/taltech-vanemarendajaks/team-01-glossaar/{component}

The image also should pop up at out repo page.


