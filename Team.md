# Team-01

## Team Members & GH Usernames

- **Daniil** --- `retr0mouse`
- **Katri** --- `katri`
- **Kenert** --- `karukenert`
- **Liisi** --- `liisieenmaa`
- **Ivo** --- `ivovaliste`
- **Anastasia** --- `asyawind`

------------------------------------------------------------------------

## Team Workflow
Our team uses feature branches for most changes, which are merged through PRs after review. Non-behavioral changes (docs, comments, formatting, typo fixes) can be merged directly to main, if the user role allows

Prerequesites for merging an PR:
 - minimum 1 approve
 - If changes are required, approval from the change requester is needed.”


If anyone requests changes to a PR, then a final green light for merge should be given by them as well. A suitable merge strategy will be chosen by the PR author. Once a branch is merged, we should delete it.

## Developmemnt practices

- Issues are created by every team member
- Issues don't have to contain every information bit necessary, but only enough information to start developing.
- Pull request are kept small and should be self-reviewd before submitting.

### BE

- Use Lombok annotations when possible to reduce boilerplate
- TODO: ...

### FE

- API requests are made via Api client
- API responses must be typed
- TODO: ...

## Testing plan

Considering the limited time resource, writing automated tests is not a priority. This can and should still be done in some reasonable extent.

Automated tests will be run:
- Only for a specific Application component when a PR is opened against it (frontend | backend)
- Run prior to creating an image

## Glossary

- PR - Pull Request
- GH - GitHub
- GHCR - GitHub Container Registry
- Issue - GH Issue, which is basis for a PR
- FE - Frontend
- BE - Backend
- Application component - 'frontend' or 'backend', an independent unit of our application
- dev - application environment for developers that we host on https://glossaar.href.ee/ . This environemnt is not meant for end user and does not give any quarantees. Data can be wiped as we wish.
