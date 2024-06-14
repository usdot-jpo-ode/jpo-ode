# Quarterly Release Process
The quarterly release process is used to prepare the code for a new release at the end of each quarter. This process includes creating a new release branch, stabilizing the code, updating project references, creating the release, and testing the release.

There are four over-arching steps to the quarterly release:
1. Code Ready & Release Notes
2. Preliminary Testing
3. Project Reference Updates & Release Creation
4. DockerHub Image Testing

## 1. Code Ready & Release Notes
### Description
The first step in the quarterly release process is to ensure that the code is ready for release and that the release notes have been created. This includes ensuring that all features and bug fixes that are intended for the release are complete and have been merged into the `develop` branch. A new branch `release_(year)-(quarter)` should be created from the `develop` branch to stabilize the code and prepare for the release. Release notes should be drafted and added to the `Release_notes.md` file in the `docs` directory of the repository.

### Acceptance Criteria
    - [ ] jpo-ode
        - [ ] Release notes drafted & added to `Release_notes.md` file in `docs` directory
        - [ ] Code changes for release are merged into `develop`
        - [ ] A new branch `release_(year)-(quarter)` is created from `develop`
    - [ ] asn1_codec
        - [ ] Release notes drafted & added to `Release_notes.md` file in `docs` directory
        - [ ] Code changes for release are merged into `develop`
        - [ ] A new branch `release_(year)-(quarter)` is created from `develop`
    - [ ] jpo-cvdp
        - [ ] Release notes drafted & added to `Release_notes.md` file in `docs` directory
        - [ ] Code changes for release are merged into `develop`
        - [ ] A new branch `release_(year)-(quarter)` is created from `develop`
    - [ ] jpo-security-svcs
        - [ ] Release notes drafted & added to `Release_notes.md` file in `docs` directory
        - [ ] Code changes for release are merged into `develop`
        - [ ] A new branch `release_(year)-(quarter)` is created from `develop`
    - [ ] jpo-sdw-depositor
        - [ ] Release notes drafted & added to `Release_notes.md` file in `docs` directory
        - [ ] Code changes for release are merged into `develop`
        - [ ] A new branch `release_(year)-(quarter)` is created from `develop`
    - [ ] jpo-s3-deposit
        - [ ] Release notes drafted & added to `Release_notes.md` file in `docs` directory
        - [ ] Code changes for release are merged into `develop`
        - [ ] A new branch `release_(year)-(quarter)` is created from `develop`
    - [ ] jpo-geojsonconverter
        - [ ] Release notes drafted & added to `Release_notes.md` file in `docs` directory
        - [ ] Code changes for release are merged into `develop`
        - [ ] A new branch `release_(year)-(quarter)` is created from `develop`
    - [ ] jpo-conflictmonitor
        - [ ] Release notes drafted & added to `Release_notes.md` file in `docs` directory
        - [ ] Code changes for release are merged into `develop`
        - [ ] A new branch `release_(year)-(quarter)` is created from `develop`
    - [ ] jpo-conflictvisualizer
        - [ ] Release notes drafted & added to `Release_notes.md` file in `docs` directory
        - [ ] Code changes for release are merged into `develop`
        - [ ] A new branch `release_(year)-(quarter)` is created from `develop`
    - [ ] jpo-cvmanager
        - [ ] Release notes drafted & added to `Release_notes.md` file in `docs` directory
        - [ ] Code changes for release are merged into `develop`
        - [ ] A new branch `release_(year)-(quarter)` is created from `develop`

## 2. Preliminary Testing
### Description
After the release branches are created, preliminary testing should be conducted to ensure that the code is stable and ready for release. This includes running unit tests, integration tests, and any other relevant tests to ensure that the code is functioning as expected.

### Acceptance Criteria
    - [ ] jpo-ode
        - [ ] code compiles
        - [ ] unit tests pass
        - [ ] program starts up correctly
        - [ ] http endpoint is reachable
        - [ ] tims can be successfully pushed to http endpoint
        - [ ] capable of ingesting messages via udp (see scripts in `scripts/tests` directory)
            - [ ] tims
            - [ ] bsms
            - [ ] ssms
            - [ ] srms
            - [ ] spats
            - [ ] maps
            - [ ] psms
    - [ ] asn1_codec
        - [ ] code compiles
        - [ ] unit tests pass
        - [ ] program starts up correctly
        - [ ] program can be configured for decoding successfully
        - [ ] program can be configured for encoding successfully
        - [ ] messages get decoded as expected
        - [ ] messages get encoded as expected
    - [ ] jpo-cvdp
        - [ ] code compiles
        - [ ] unit tests pass
        - [ ] program starts up correctly
        - [ ] messages get consumed as expected
        - [ ] BSMs inside geofence are retained
        - [ ] BSMs with a partII section are retained
        - [ ] BSMs outside geofence are suppressed
        - [ ] BSMs above speed range are suppressed
        - [ ] BSMs below speed range are suppressed
    - [ ] jpo-security-svcs
        - [ ] code compiles
        - [ ] program starts up correctly
        - [ ] program can be successfully configured
        - [ ] messages can be successfully signed
    - [ ] jpo-sdw-depositor
        - [ ] code compiles
        - [ ] unit tests pass
        - [ ] program starts up correctly
        - [ ] messages are consumed successfully
        - [ ] messages are submitted to the SDX successfully
    - [ ] jpo-s3-deposit
        - [ ] code compiles
        - [ ] program starts up correctly
        - [ ] deposits can be made to one of the destinations successfully
    - [ ] jpo-geojsonconverter
        - [ ] code compiles
        - [ ] unit tests pass
        - [ ] program starts up correctly
        - [ ] program can be configured successfully
        - [ ] MAP & SPaT messages are consumed successfully
        - [ ] valid ProcessedMaps & ProcessedSpats are outputted
    - [ ] jpo-conflictmonitor
        - [ ] code compiles
        - [ ] unit tests pass
        - [ ] program starts up correctly
        - [ ] program processes SpAT/MAP/BSM messages and generates events as expected (see https://github.com/usdot-jpo-ode/jpo-conflictmonitor/wiki/Integration-Tests)
            - [ ] test BSM events
            - [ ] test connection of travel event
            - [ ] test intersection reference alignment events
            - [ ] test lane direction of travel event
            - [ ] test MAP broadcast rate event
            - [ ] test MAP minimum data event
            - [ ] test signal group alignment events
            - [ ] test signal state conflict events
            - [ ] test SPaT broadcast rate event
            - [ ] test SPaT minimum data event
            - [ ] test SPaT time change details event
            - [ ] test stop line passage events
            - [ ] test stop line stop events
    - [ ] jpo-cvmanager
        - [ ] code compiles
        - [ ] unit tests pass
        - [ ] program starts up correctly
        - [ ] webapp can be signed into successfully
    - [ ] jpo-conflictvisualizer
        - [ ] code compiles
        - [ ] unit tests pass
        - [ ] program starts up correctly
        - [ ] GUI functions & can display messages

## 3. Project Reference Updates & Release Creation
### Description
After preliminary testing is complete, project reference updates should be made to ensure that all projects are referencing the correct versions of other projects. Once project references are updated, the release should be created by merging the `release_(year)-(quarter)` branch into the `master` branch and tagging the release with the appropriate version number. Images should be built and pushed to DockerHub for testing.

### Steps
#### Merging release branches, updating project references and creating releases
1. Merge ‘release_(year)-(quarter)’ branch into ‘master/main’ branch for the following projects:
    - asn1_codec
    - jpo-cvdp
    - jpo-security-svcs
    - jpo-sdw-depositor
    - jpo-s3-deposit

    1a. Create a release for the asn1_codec project from the ‘master/main’ branch and tag the release with the version number of the release. (e.g. asn1_codec-x.x.x)
    1b. Create a release for the jpo-cvdp project from the ‘master/main’ branch and tag the release with the version number of the release. (e.g. jpo-cvdp-x.x.x)
    1c. Create a release for the jpo-security-svcs project from the ‘master/main’ branch and tag the release with the version number of the release. (e.g. jpo-security-svcs-x.x.x)
    1d. Create a release for the jpo-sdw-depositor project from the ‘master/main’ branch and tag the release with the version number of the release. (e.g. jpo-sdw-depositor-x.x.x)
    1e. Create a release for the jpo-s3-deposit project from the ‘master/main’ branch and tag the release with the version number of the release. (e.g. jpo-s3-deposit-x.x.x)

2. Update git submodule references for the ‘jpo-ode’ project to point to tagged commits in projects with updated `master/main` branches. Also update the version numbers within the pom.xmls of each of the ode subprojects (jpo-ode-common, jpo-ode-plugins, jpo-ode-svcs) to be self-consistent.
    
    2a. Open the jpo-ode project in an IDE and update the version numbers in the pom.xml files of the jpo-ode-common, jpo-ode-plugins, and jpo-ode-svcs projects to match the version number of the release. (e.g. 1.0.0)
    2b. Navigate to the asn1_codec directory and run `git checkout tags/asn1_codec-x.x.x` to update the submodule reference.
    2c. Navigate to the jpo-cvdp directory and run `git checkout tags/jpo-cvdp-x.x.x` to update the submodule reference.
    2d. Navigate to the jpo-security-svcs directory and run `git checkout tags/jpo-security-svcs-x.x.x` to update the submodule reference.
    2e. Navigate to the jpo-sdw-depositor directory and run `git checkout tags/jpo-sdw-depositor-x.x.x` to update the submodule reference.
    2f. Navigate to the jpo-s3-deposit directory and run `git checkout tags/jpo-s3-deposit-x.x.x` to update the submodule reference. 
    2g. Commit these changes to the `release_(year)-(quarter)` branch & push the changes to the remote repository.
    2h. Ensure these changes pass CI/CD checks before continuing.

3. Merge `release_(year)-(quarter)` branch into `master/main` branch for the jpo-ode project, and create a release with the version number of the release. (e.g. jpo-ode-x.x.x)

4. Update git submodule references for the ‘jpo-geojsonconverter’ project to point to the tagged commit in jpo-ode master/main branch. Also update the corresponding version number for the jpo-ode dependency in the pom.xml of the geojsonconverter project.

    4a. Open the jpo-geojsonconverter project in an IDE.
    4b. Navigate to the jpo-ode directory and run `git checkout tags/jpo-ode-x.x.x` to update the submodule reference.
    4c. Update the version number in the pom.xml for the jpo-ode dependency to match the version number of the release. (e.g. 1.0.0)
    4d. Commit these changes to the `release_(year)-(quarter)` branch & push the changes to the remote repository.
    4e. Ensure these changes pass CI/CD checks before continuing.

5. Merge `release_(year)-(quarter)` branch into `master/main` branch for the jpo-geojsonconverter project, and create a release with the version number of the release. (e.g. jpo-geojsonconverter-x.x.x)

6. Update git submodule references for the `jpo-conflictmonitor` project to point to the tagged commit in jpo-geojsonconverter master/main branch. Also update the corresponding version number for the jpo-geojsonconverter dependency in the pom.xml files of the conflictmonitor project. This change will be necessary in the jpo-conflictmonitor/pom.xml, jpo-deduplicator/pom.xml and message-sender/pom.xml files.

    6a. Open the jpo-conflictmonitor project in an IDE.
    6b. Navigate to the jpo-geojsonconverter directory and run `git checkout tags/jpo-geojsonconverter-x.x.x` to update the submodule reference.
    6c. Update the version number in the pom.xml filesfor the jpo-geojsonconverter dependency to match the version number of the release. (e.g. 1.0.0)
    6d. Commit these changes to the `release_(year)-(quarter)` branch & push the changes to the remote repository.
    6e. Ensure these changes pass CI/CD checks before continuing.

7. Merge `release_(year)-(quarter)` branch into `master/main` branch for the jpo-conflictmonitor project, and create a release with the version number of the release. (e.g. jpo-conflictmonitor-x.x.x)

8. Update git submodule references for the `jpo-conflictvisualizer` project to point to the tagged commit in jpo-conflictmonitor master/main branch. Also update the corresponding version number for the jpo-conflictmonitor dependency in the pom.xml file of the conflictvisualizer project.

    8a. Open the jpo-conflictvisualizer project in an IDE.
    8b. Navigate to the jpo-conflictmonitor directory and run `git checkout tags/jpo-conflictmonitor-x.x.x` to update the submodule reference.
    8c. Update the version number in the pom.xml files for the jpo-conflictmonitor dependency to match the version number of the release. (e.g. 1.0.0)
    8d. Update the version number in the pom.xml file for the jpo-geojsonconverter dependency to match the version number of the release. (e.g. 1.0.0)
    8e. Update the version number in the pom.xml file for the jpo-ode dependency to match the version number of the release. (e.g. 1.0.0)
    8f. Commit these changes to the `release_(year)-(quarter)` branch & push the changes to the remote repository.
    8g. Ensure these changes pass CI/CD checks before continuing.

9. Merge `release_(year)-(quarter)` branch into `master/main` branch for the jpo-conflictvisualizer project, and create a release with the version number of the release. (e.g. jpo-conflictvisualizer-x.x.x)

10. Merge `release_(year)-(quarter)` branch into `master/main` branch for the jpo-cvmanager project, and create a release with the version number of the release. (e.g. jpo-cvmanager-x.x.x)

#### Create Docker Images
1. Within the github CI/CD release process, use the releases for each application to produce docker images with the same tag name, containing the version number of each app. For example, the jpo-ode release will produce a docker image with the version number of the release (e.g. 1.0.0).
    
    1a. The Conflict Visualizer will need two separate images to be created: one for the API and one for Keycloak.

2. Upload docker images to [DockerHub](https://hub.docker.com/u/usdotjpoode).
3. Tag docker images with the version number of each app. (e.g. 1.0.0)
4. Tag docker images with year and quarter of release. (e.g. 2024-Q2)
5. Tag docker images with 'latest' tag for the most recent release.

#### Housekeeping
1. Merge master branches into develop branches for each project & verify that CI/CD passes.

## 4. DockerHub Image Testing
### Description
After the docker images have been built and pushed to DockerHub, they should be tested to ensure that they are functioning as expected. This includes running the docker images locally and verifying that the applications are working correctly.

### Acceptance Criteria
    - [ ] jpo-ode
        - [ ] image starts up correctly
        - [ ] http endpoint is reachable
        - [ ] tims can be successfully pushed to http endpoint
        - [ ] capable of ingesting messages via udp (see scripts in `scripts/tests` directory)
            - [ ] tims
            - [ ] bsms
            - [ ] ssms
            - [ ] srms
            - [ ] spats
            - [ ] maps
            - [ ] psms
    - [ ] asn1_codec
        - [ ] image starts up correctly
        - [ ] program can be configured for decoding successfully
        - [ ] program can be configured for encoding successfully
        - [ ] messages get decoded as expected
        - [ ] messages get encoded as expected
    - [ ] jpo-cvdp
        - [ ] image starts up correctly
        - [ ] messages get consumed as expected
        - [ ] BSMs inside geofence are retained
        - [ ] BSMs with a partII section are retained
        - [ ] BSMs outside geofence are suppressed
        - [ ] BSMs above speed range are suppressed
        - [ ] BSMs below speed range are suppressed
    - [ ] jpo-security-svcs
        - [ ] image starts up correctly
        - [ ] program can be successfully configured
        - [ ] messages can be successfully signed
    - [ ] jpo-sdw-depositor
        - [ ] image starts up correctly
        - [ ] messages are consumed successfully
        - [ ] messages are submitted to the SDX successfully
    - [ ] jpo-s3-deposit
        - [ ] image starts up correctly
        - [ ] deposits can be made to one of the destinations successfully
    - [ ] jpo-geojsonconverter
        - [ ] image starts up correctly
        - [ ] program can be configured successfully
        - [ ] MAP & SPaT messages are consumed successfully
        - [ ] valid ProcessedMaps & ProcessedSpats are outputted
    - [ ] jpo-conflictmonitor
        - [ ] image starts up correctly
        - [ ] program processes SpAT/MAP/BSM messages and generates events as expected (see https://github.com/usdot-jpo-ode/jpo-conflictmonitor/wiki/Integration-Tests)
            - [ ] test BSM events
            - [ ] test connection of travel event
            - [ ] test intersection reference alignment events
            - [ ] test lane direction of travel event
            - [ ] test MAP broadcast rate event
            - [ ] test MAP minimum data event
            - [ ] test signal group alignment events
            - [ ] test signal state conflict events
            - [ ] test SPaT broadcast rate event
            - [ ] test SPaT minimum data event
            - [ ] test SPaT time change details event
            - [ ] test stop line passage events
            - [ ] test stop line stop events
    - [ ] jpo-conflictvisualizer-api
        - [ ] image starts up correctly
        - [ ] GUI functions & can display messages
    - [ ] jpo-conflictvisualizer-keycloak
        - [ ] image starts up correctly
        - [ ] authentication verified to work

At this point the quarterly release process is complete.

# Standalone Hotfix Release Process
The standalone hotfix release process is used to address critical issues that require immediate attention. This process is similar to the quarterly release process, but is expedited to address the critical issue as quickly as possible.

It should be noted that not all projects will be necessarily affected by a hotfix. The dependent projects that are affected by the hotfix should be updated and released, while the other projects should remain unchanged.

There are two over-arching steps to the standalone hotfix release:
1. Code Ready & Release Notes
2. Project Reference Updates & Release Creation

## 1. Code Ready & Release Notes
### Description
The first step in the standalone hotfix release process is to create a new branch from the `master` branch to address the critical issue. The code changes should be merged into the hotfix branch and release notes should be drafted and added to the `Release_notes.md` file in the `docs` directory of the repository.

### Acceptance Criteria
    - [ ] A new branch `hotfix_(year)-(month)-(day)` is created from `master` for the project requiring the hotfix
    - [ ] Patch version number is updated in the `pom.xml` file of the project requiring the hotfix
    - [ ] Release notes drafted & added to `Release_notes.md` file in `docs` directory
    - [ ] Code changes for hotfix are merged into `hotfix_(year)-(month)-(day)`

## 2. Project Reference Updates & Release Creation
### Description
After the hotfix branch is created and the code changes are merged, project reference updates should be made to ensure that all projects are referencing the correct versions of other projects. Once project references are updated, the release should be created by merging the `hotfix_(year)-(month)-(day)` branch into the `master` branch and tagging the release with the appropriate version number. Images should be built and pushed to DockerHub for testing.

### Steps
#### Merging hotfix branches & updating project references
1. Merge `hotfix_(year)-(month)-(day)` branch into `master/main` branch for the project requiring the hotfix.
2. Tag the master/main branch of the project with a git tag that includes the version number of the hotfix.
3. Update git submodule references & pom.xml references for dependent projects to point to tagged commits in projects with updated `master/main` branches.
4. Merge `hotfix_(year)-(month)-(day)` branch into `master/main` branch for the dependent projects, and add a git tag with the version number of the hotfix.

#### Create Releases & Docker Images
1. Within the github CI/CD release process, use the release tags for each affected application to produce releases and docker images with the same tag name, containing the version number of each app.
2. Upload docker images to [DockerHub](https://hub.docker.com/u/usdotjpoode).
3. Tag docker images with the version number of each app. (e.g. 1.0.0)
4. Tag docker images with year, month, and day of hotfix. (e.g. 2024-04-01)
5. Tag docker images with 'latest' tag for the most recent release.

#### Housekeeping
1. Merge master branches into develop branches for each affected project & verify that CI/CD passes.

At this point the standalone hotfix release process is complete.
