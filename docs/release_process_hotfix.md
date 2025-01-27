# JPO Standalone Hotfix Release Process
The standalone hotfix release process is used to address critical issues that require immediate attention. This process is similar to the quarterly release process, but is expedited to address the critical issue as quickly as possible.

It should be noted that not all projects will be necessarily affected by a hotfix. The dependent projects that are affected by the hotfix should be updated and released, while the other projects should remain unchanged.

There are two over-arching steps to the standalone hotfix release:
1. Code Ready & Release Notes
2. Project Reference Updates & Release Creation

## 1. Code Ready & Release Notes
### Description
The first step in the standalone hotfix release process is to create a new branch from the `master` branch to address the critical issue. The code changes should be merged into the hotfix branch and release notes should be drafted and added to the `Release_notes.md` file in the `docs` directory of the repository.

### Acceptance Criteria
    - [ ] A new branch `hotfix/(year)-(month)-(day)` is created from `master` for the project requiring the hotfix
    - [ ] Patch version number is updated in the `pom.xml` file of the project requiring the hotfix
    - [ ] Release notes drafted & added to `Release_notes.md` file in `docs` directory
    - [ ] Code changes for hotfix are merged into `hotfix/(year)-(month)-(day)`

## 2. Project Reference Updates & Release Creation
### Description
After the hotfix branch is created and the code changes are merged, project reference updates should be made to ensure that all projects are referencing the correct versions of other projects. Once project references are updated, the release should be created by merging the `hotfix/(year)-(month)-(day)` branch into the `master` branch and tagging the release with the appropriate version number. Images should be built and pushed to DockerHub for testing.

### Steps
#### Merging hotfix branches & updating project references
1. Merge `hotfix/(year)-(month)-(day)` branch into `master/main` branch for the project requiring the hotfix.
2. Tag the master/main branch of the project with a git tag that includes the version number of the hotfix.
3. Update git submodule references & pom.xml references for dependent projects to point to tagged commits in projects with updated `master/main` branches.
4. Merge `hotfix/(year)-(month)-(day)` branch into `master/main` branch for the dependent projects, and add a git tag with the version number of the hotfix.

#### Create Releases & Docker Images
1. Within the github CI/CD release process, use the release tags for each affected application to produce releases and docker images with the same tag name, containing the version number of each app.
2. Upload docker images to [DockerHub](https://hub.docker.com/u/usdotjpoode).
3. Tag docker images with the version number of each app. (e.g. 1.0.0)
4. Tag docker images with year, month, and day of hotfix. (e.g. 2024-04-01)
5. Tag docker images with 'latest' tag for the most recent release.

#### Housekeeping
1. Merge master branches into develop branches for each affected project & verify that CI/CD passes.

At this point the standalone hotfix release process is complete.
