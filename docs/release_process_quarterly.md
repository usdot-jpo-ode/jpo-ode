# JPO Quarterly Release Process
The quarterly release process is designed to prepare the code for a new release at the end of each quarter. This process involves several steps: creating a new release branch, stabilizing the code, updating project references, building the release, and testing it. These steps are performed sequentially for each project, starting with those that have no dependencies and progressing outward through the dependency chain.

The order of project releases is as follows:
1. [asn1_codec](#asn1_codec)
2. [jpo-cvdp](#jpo-cvdp)
3. [jpo-security-svcs](#jpo-security-svcs)
4. [jpo-sdw-depositor](#jpo-sdw-depositor)
5. [jpo-utils](#jpo-utils)
6. [jpo-ode](#jpo-ode)
7. [jpo-geojsonconverter](#jpo-geojsonconverter)
8. [jpo-conflictmonitor](#jpo-conflictmonitor)
9. [jpo-conflictvisualizer](#jpo-conflictvisualizer)
10. [jpo-deduplicator](#jpo-deduplicator)
11. [jpo-cvmanager](#jpo-cvmanager)
12. [jpo-s3-deposit](#jpo-s3-deposit)

## asn1_codec
### Prerequisites
None

### 1. Code Ready & Release Notes
    - [ ] Release notes drafted & added to `Release_notes.md` file in `docs` directory
    - [ ] Code changes for release are merged into `develop`
    - [ ] A new branch `release/(year)-(quarter)` is created from `develop`

### 2. Preliminary Testing
    - [ ] code compiles
    - [ ] unit tests pass
    - [ ] program starts up correctly
    - [ ] program can be configured for decoding successfully
    - [ ] program can be configured for encoding successfully
    - [ ] messages get decoded as expected
    - [ ] messages get encoded as expected

### 3. Project Reference Updates & Release Creation
    - [ ] Merge ‘release/(year)-(quarter)’ branch into ‘master’ branch for the asn1_codec project
    - [ ] Create a release for the asn1_codec project from the ‘master’ branch and tag the release with the version number of the release. (e.g. asn1_codec-x.x.x)
    - [ ] Create docker image
        - [ ] Use the release for the asn1_codec to produce docker image with the same tag name, containing the version number.
        - [ ] Upload docker image to [DockerHub](https://hub.docker.com/u/usdotjpoode)
        - [ ] Tag docker image with the version number of the app. (e.g. 1.0.0)
        - [ ] Tag docker image with year and quarter of release. (e.g. 2024-Q2)
        - [ ] Tag docker image with 'latest' tag for the most recent release.
    - [ ] Merge master branch into develop branch & verify that CI/CD passes.

### 4. DockerHub Image Testing
    - [ ] image starts up correctly
    - [ ] program can be configured for decoding successfully
    - [ ] program can be configured for encoding successfully
    - [ ] messages get decoded as expected
    - [ ] messages get encoded as expected


## jpo-cvdp
### Prerequisites
None

### 1. Code Ready & Release Notes
    - [ ] Release notes drafted & added to `Release_notes.md` file in `docs` directory
    - [ ] Code changes for release are merged into `develop`
    - [ ] A new branch `release/(year)-(quarter)` is created from `develop`

### 2. Preliminary Testing
    - [ ] code compiles
    - [ ] unit tests pass
    - [ ] program starts up correctly
    - [ ] messages get consumed as expected
    - [ ] BSMs inside geofence are retained
    - [ ] BSMs with a partII section are retained
    - [ ] BSMs outside geofence are suppressed
    - [ ] BSMs above speed range are suppressed
    - [ ] BSMs below speed range are suppressed

### 3. Project Reference Updates & Release Creation
    - [ ] Merge ‘release/(year)-(quarter)’ branch into ‘master’ branch for the jpo-cvdp project
    - [ ] Create a release for the jpo-cvdp project from the ‘master’ branch and tag the release with the version number of the release. (e.g. jpo-cvdp-x.x.x)
    - [ ] Create docker image
        - [ ] Use the release for the jpo-cvdp to produce docker image with the same tag name, containing the version number.
        - [ ] Upload docker image to [DockerHub](https://hub.docker.com/u/usdotjpoode)
        - [ ] Tag docker image with the version number of the app. (e.g. 1.0.0)
        - [ ] Tag docker image with year and quarter of release. (e.g. 2024-Q2)
        - [ ] Tag docker image with 'latest' tag for the most recent release.
    - [ ] Merge master branch into develop branch & verify that CI/CD passes.

### 4. DockerHub Image Testing
    - [ ] image starts up correctly
    - [ ] messages get consumed as expected
    - [ ] BSMs inside geofence are retained
    - [ ] BSMs with a partII section are retained
    - [ ] BSMs outside geofence are suppressed
    - [ ] BSMs above speed range are suppressed
    - [ ] BSMs below speed range are suppressed


## jpo-security-svcs
### Prerequisites
None

### 1. Code Ready & Release Notes
    - [ ] Release notes drafted & added to `Release_notes.md` file in `docs` directory
    - [ ] Code changes for release are merged into `develop`
    - [ ] A new branch `release/(year)-(quarter)` is created from `develop`

### 2. Preliminary Testing
    - [ ] code compiles
    - [ ] program starts up correctly
    - [ ] program can be successfully configured
    - [ ] messages can be successfully signed

### 3. Project Reference Updates & Release Creation
    - [ ] Update version number in pom.xml if not already done
    - [ ] Merge ‘release/(year)-(quarter)’ branch into ‘master’ branch for the jpo-security-svcs project
    - [ ] Create a release for the jpo-security-svcs project from the ‘master’ branch and tag the release with the version number of the release. (e.g. jpo-security-svcs-x.x.x)
    - [ ] Create docker image
        - [ ] Use the release for the jpo-security-svcs to produce docker image with the same tag name, containing the version number.
        - [ ] Upload docker image to [DockerHub](https://hub.docker.com/u/usdotjpoode)
        - [ ] Tag docker image with the version number of the app. (e.g. 1.0.0)
        - [ ] Tag docker image with year and quarter of release. (e.g. 2024-Q2)
        - [ ] Tag docker image with 'latest' tag for the most recent release.
    - [ ] Merge master branch into develop branch & verify that CI/CD passes.

### 4. DockerHub Image Testing
    - [ ] image starts up correctly
    - [ ] program can be successfully configured
    - [ ] messages can be successfully signed


## jpo-sdw-depositor
### Prerequisites
None

### 1. Code Ready & Release Notes
    - [ ] Release notes drafted & added to `Release_notes.md` file in `docs` directory
    - [ ] Code changes for release are merged into `develop`
    - [ ] A new branch `release/(year)-(quarter)` is created from `develop`

### 2. Preliminary Testing
    - [ ] code compiles
    - [ ] unit tests pass
    - [ ] program starts up correctly
    - [ ] messages are consumed successfully
    - [ ] messages are submitted to the SDX successfully

### 3. Project Reference Updates & Release Creation
    - [ ] Update version number in pom.xml if not already done
    - [ ] Merge ‘release/(year)-(quarter)’ branch into ‘master’ branch for the jpo-sdw-depositor project
    - [ ] Create a release for the jpo-sdw-depositor project from the ‘master’ branch and tag the release with the version number of the release. (e.g. jpo-sdw-depositor-x.x.x)
    - [ ] Create docker image
        - [ ] Use the release for the jpo-sdw-depositor to produce docker image with the same tag name, containing the version number.
        - [ ] Upload docker image to [DockerHub](https://hub.docker.com/u/usdotjpoode)
        - [ ] Tag docker image with the version number of the app. (e.g. 1.0.0)
        - [ ] Tag docker image with year and quarter of release. (e.g. 2024-Q2)
        - [ ] Tag docker image with 'latest' tag for the most recent release.
    - [ ] Merge master branch into develop branch & verify that CI/CD passes.

### 4. DockerHub Image Testing
    - [ ] image starts up correctly
    - [ ] messages are consumed successfully
    - [ ] messages are submitted to the SDX successfully


## jpo-utils
### Prerequisites
None

### 1. Code Ready & Release Notes
    - [ ] Release notes drafted & added to `Release_notes.md` file in `docs` directory
    - [ ] Code changes for release are merged into `develop`
    - [ ] A new branch `release/(year)-(quarter)` is created from `develop`

### 2. Preliminary Testing
    - [ ] jpo-jikkou docker image builds
    - [ ] jpo-kafka-connect image builds
        - [ ] Verify all connectors are created using connect rest api

### 3. Project Reference Updates & Release Creation
    - [ ] Update version number in pom.xml files for the 'jpo-utils' project if not already done.
    - [ ] Create a release for the jpo-utils project from the ‘master’ branch and tag the release with the version number of the release. (e.g. jpo-utils-x.x.x)
    - [ ] Merge master branch into develop branch & verify that CI/CD passes.

### 4. DockerHub Image Testing
    - [ ] Change the image references in `docker-compose-kafka.yml` and `docker-compose-connect.yml` to the version number of the release. (e.g. 1.0.0)
        - [ ] `docker-compose-kafka.yml` services changed:
            - [ ] kafka-setup image: `usdotjpoode/jpo-jikkou:1.0.0`
        - [ ] `docker-compose-connect.yml` services changed:
            - [ ] kafka-connect image: `usdotjpoode/jpo-kafka-connect:1.0.0`
            - [ ] kafka-connect-setup image: `usdotjpoode/jpo-jikkou:1.0.0`
    - [ ] jpo-jikkou docker image runs successfully
    - [ ] jpo-kafka-connect image runs successfully
        - [ ] Verify all connectors are created using connect rest api

## jpo-ode
### Prerequisites
    - [ ] asn1_codec release completed
    - [ ] jpo-cvdp release completed
    - [ ] jpo-security-svcs release completed
    - [ ] jpo-sdw-depositor release completed
    - [ ] jpo-utils release completed

#### Dependency Types
| Dependency | Type |
| --- | --- |
| asn1_codec | Git Submodule |
| jpo-cvdp | Git Submodule |
| jpo-security-svcs | Git Submodule |
| jpo-sdw-depositor | Git Submodule |
| jpo-utils | Git Submodule |

### 1. Code Ready & Release Notes
    - [ ] Release notes drafted & added to `Release_notes.md` file in `docs` directory
    - [ ] Code changes for release are merged into `develop`
    - [ ] A new branch `release/(year)-(quarter)` is created from `develop`

### 2. Preliminary Testing
    - [ ] code compiles
    - [ ] unit tests pass
    - [ ] program starts up correctly
    - [ ] http endpoint is reachable
    - [ ] TIMs can be successfully pushed to http endpoint
    - [ ] capable of ingesting messages via udp (see scripts in `scripts/tests` directory)
        - [ ] TIMs
        - [ ] BSMs
        - [ ] SSMs
        - [ ] SRMs
        - [ ] SPaTs
        - [ ] Maps
        - [ ] PSMs
### 3. Project Reference Updates & Release Creation
    - [ ] Update version number in pom.xml files for the 'jpo-ode' project if not already done.
        - [ ] Open the jpo-ode project in an IDE
        - [ ] Update the version number in the pom.xml files of the jpo-ode-common, jpo-ode-plugins, and jpo-ode-svcs projects to match the version number of the release. (e.g. 1.0.0)
        - [ ] Update the dependencies in pom.xml files of the jpo-ode-common, jpo-ode-plugins, and jpo-ode-svcs projects to the version number set in the previous step. The `jpo-ode-plugins` depends on the `jpo-ode-common` project, and the `jpo-ode-svcs` project depends on the `jpo-ode-core` and `jpo-ode-plugins` projects. These should all be referencing the same version number. (e.g. 1.0.0)
    - [ ] Update git submodule references for the ‘jpo-ode’ project to point to tagged commits in projects with updated `master` branches.
        - [ ] Open the jpo-ode project in an IDE.
        - [ ] Navigate to the asn1_codec directory and run `git checkout tags/asn1_codec-x.x.x` to update the submodule reference.
        - [ ] Navigate to the jpo-cvdp directory and run `git checkout tags/jpo-cvdp-x.x.x` to update the submodule reference.
        - [ ] Navigate to the jpo-security-svcs directory and run `git checkout tags/jpo-security-svcs-x.x.x` to update the submodule reference.
        - [ ] Navigate to the jpo-sdw-depositor directory and run `git checkout tags/jpo-sdw-depositor-x.x.x` to update the submodule reference.
        - [ ] Navigate to the jpo-utils directory and run `git checkout tags/jpo-utils-x.x.x` to update the submodule reference. 
        - [ ] Commit these changes to the `release/(year)-(quarter)` branch & push the changes to the remote repository.
        - [ ] Ensure these changes pass CI/CD checks before continuing.
    - [ ] Merge `release/(year)-(quarter)` branch into `master` branch for the jpo-ode project, and create a release with the version number of the release. (e.g. jpo-ode-x.x.x)
    - [ ] Create docker image
        - [ ] Use the release for the jpo-ode to produce docker image with the same tag name, containing the version number.
        - [ ] Upload docker image to [DockerHub](https://hub.docker.com/u/usdotjpoode)
        - [ ] Tag docker image with the version number of the app. (e.g. 1.0.0)
        - [ ] Tag docker image with year and quarter of release. (e.g. 2024-Q2)
        - [ ] Tag docker image with 'latest' tag for the most recent release.
    - [ ] Merge master branch into develop branch & verify that CI/CD passes.

### 4. DockerHub Image Testing
    - [ ] image starts up correctly
    - [ ] http endpoint is reachable
    - [ ] TIMs can be successfully pushed to http endpoint
    - [ ] capable of ingesting messages via udp (see scripts in `scripts/tests` directory)
        - [ ] TIMs
        - [ ] BSMs
        - [ ] SSMs
        - [ ] SRMs
        - [ ] SPaTs
        - [ ] Maps
        - [ ] PSMs


## jpo-geojsonconverter
### Prerequisites
    - [ ] jpo-ode release completed

#### Dependency Types
| Dependency | Type |
| --- | --- |
| jpo-ode | Maven, DockerHub |

### 1. Code Ready & Release Notes
    - [ ] Release notes drafted & added to `Release_notes.md` file in `docs` directory
    - [ ] Code changes for release are merged into `develop`
    - [ ] A new branch `release/(year)-(quarter)` is created from `develop`

### 2. Preliminary Testing
    - [ ] code compiles
    - [ ] unit tests pass
    - [ ] program starts up correctly
    - [ ] program can be configured successfully
    - [ ] Map, SPaT, & BSM messages are consumed successfully
    - [ ] valid ProcessedMaps, ProcessedSpats, and ProcessedMaps are outputted

### 3. Project Reference Updates & Release Creation
    - [ ] Update version number in pom.xml file for the `jpo-geojsonconverter` project if not already done.
    - [ ] Update git submodule & artifact references for the ‘jpo-geojsonconverter’ project.
        - [ ] Open the jpo-geojsonconverter project in an IDE.
        - [ ] Navigate to the jpo-utils directory and run `git checkout tags/jpo-utils-x.x.x` to update the submodule reference.
        - [ ] Update the version number in the pom.xml for the jpo-ode-core and jpo-ode-plugins dependencies to match the version number of the corresponding releases. (e.g. 1.0.0)
        - [ ] Commit these changes to the `release/(year)-(quarter)` branch & push the changes to the remote repository.
        - [ ] Ensure these changes pass CI/CD checks before continuing.
    - [ ] Merge `release/(year)-(quarter)` branch into `master` branch for the jpo-geojsonconverter project, and create a release with the version number of the release. (e.g. jpo-geojsonconverter-x.x.x)
    - [ ] Create docker image
        - [ ] Use the release for the jpo-geojsonconverter to produce docker image with the same tag name, containing the version number.
        - [ ] Upload docker image to [DockerHub](https://hub.docker.com/u/usdotjpoode)
        - [ ] Tag docker image with the version number of the app. (e.g. 1.0.0)
        - [ ] Tag docker image with year and quarter of release. (e.g. 2024-Q2)
        - [ ] Tag docker image with 'latest' tag for the most recent release.
    - [ ] Merge master branch into develop branch & verify that CI/CD passes.

### 4. DockerHub Image Testing
    - [ ] image starts up correctly
    - [ ] program can be configured successfully
    - [ ] Map, SPaT, & BSM messages are consumed successfully  
    - [ ] valid ProcessedMaps, ProcessedSpats, and ProcessedMaps are outputted


## jpo-conflictmonitor
### Prerequisites
    - [ ] jpo-ode release completed
    - [ ] jpo-geojsonconverter release completed

#### Dependency Types
| Dependency | Type |
| --- | --- |
| jpo-ode | Maven, DockerHub |
| jpo-geojsonconverter | Maven, DockerHub |

### 1. Code Ready & Release Notes
    - [ ] Release notes drafted & added to `Release_notes.md` file in `docs` directory
    - [ ] Code changes for release are merged into `develop`
    - [ ] A new branch `release/(year)-(quarter)` is created from `develop`

### 2. Preliminary Testing
    - [ ] code compiles
    - [ ] unit tests pass
    - [ ] program starts up correctly
    - [ ] program processes SpAT/Map/BSM messages and generates events as expected (see https://github.com/usdot-jpo-ode/jpo-conflictmonitor/wiki/Integration-Tests)
        - [ ] test BSM events
        - [ ] test connection of travel event
        - [ ] test intersection reference alignment events
        - [ ] test lane direction of travel event
        - [ ] test Map broadcast rate event
        - [ ] test Map minimum data event
        - [ ] test signal group alignment events
        - [ ] test signal state conflict events
        - [ ] test SPaT broadcast rate event
        - [ ] test SPaT minimum data event
        - [ ] test SPaT time change details event
        - [ ] test stop line passage events
        - [ ] test stop line stop events

### 3. Project Reference Updates & Release Creation
    - [ ] Update version number in pom.xml file for the `jpo-conflictmonitor` project if not already done.
    - [ ] Update git submodule & artifact references for the ‘jpo-conflictmonitor project.
        - [ ] Open the jpo-conflictmonitor project in an IDE.
        - [ ] Navigate to the jpo-utils directory and run `git checkout tags/jpo-utils-x.x.x` to update the submodule reference.
        - [ ] Update the version number in the pom.xml files for the jpo-geojsonconverter and jpo-ode-* dependencies to match the version number of the corresponding releases. (e.g. 1.0.0)
            - [ ] Update the jpo-conflictmonitor/pom.xml
            - [ ] Update the message-sender/pom.xml
        - [ ] Commit these changes to the `release/(year)-(quarter)` branch & push the changes to the remote repository.
        - [ ] Ensure these changes pass CI/CD checks before continuing.
    - [ ] Merge `release/(year)-(quarter)` branch into `master` branch for the jpo-conflictmonitor project, and create a release with the version number of the release. (e.g. jpo-conflictmonitor-x.x.x)
    - [ ] Create docker image
        - [ ] Use the release for the jpo-conflictmonitor to produce docker image with the same tag name, containing the version number.
        - [ ] Upload docker image to [DockerHub](https://hub.docker.com/u/usdotjpoode)
        - [ ] Tag docker image with the version number of the app. (e.g. 1.0.0)
        - [ ] Tag docker image with year and quarter of release. (e.g. 2024-Q2)
        - [ ] Tag docker image with 'latest' tag for the most recent release.
    - [ ] Merge master branch into develop branch & verify that CI/CD passes.

### 4. DockerHub Image Testing
    - [ ] image starts up correctly
    - [ ] program processes SpAT/Map/BSM messages and generates events as expected (see https://github.com/usdot-jpo-ode/jpo-conflictmonitor/wiki/Integration-Tests)
        - [ ] test BSM events
        - [ ] test connection of travel event
        - [ ] test intersection reference alignment events
        - [ ] test lane direction of travel event
        - [ ] test Map broadcast rate event
        - [ ] test Map minimum data event
        - [ ] test signal group alignment events
        - [ ] test signal state conflict events
        - [ ] test SPaT broadcast rate event
        - [ ] test SPaT minimum data event
        - [ ] test SPaT time change details event
        - [ ] test stop line passage events
        - [ ] test stop line stop events


## jpo-conflictvisualizer
### Prerequisites
    - [ ] jpo-ode release completed
    - [ ] jpo-geojsonconverter release completed
    - [ ] jpo-conflictmonitor release completed
    - [ ] asn1_codec release completed

#### Dependency Types
| Dependency | Type |
| --- | --- |
| jpo-ode | Maven, DockerHub |
| jpo-geojsonconverter | Maven, DockerHub |
| jpo-conflictmonitor | Maven, DockerHub |
| asn1_codec | Git Submodule |

### 1. Code Ready & Release Notes
    - [ ] Release notes drafted & added to `Release_notes.md` file in `docs` directory
    - [ ] Code changes for release are merged into `develop`
    - [ ] A new branch `release/(year)-(quarter)` is created from `develop`

### 2. Preliminary Testing
    - [ ] code compiles
    - [ ] unit tests pass
    - [ ] program starts up correctly
    - [ ] GUI functions & can display messages

### 3. Project Reference Updates & Release Creation
    - [ ] Update version number in pom.xml file for the `jpo-conflictvisualizer` project if not already done. The pom.xml can be found in the `api/jpo-conflictvisualizer-api` directory.
    - [ ] Update git submodule & artifact references for the 'jpo-conflictvisualizer' project.
        - [ ] Open the jpo-conflictvisualizer project in an IDE.
        - [ ] Navigate to the jpo-utils directory and run `git checkout tags/jpo-utils-x.x.x` to update the submodule reference.
        - [ ] Update the version number in the api/jpo-conflictvisualizer-api/pom.xml file for the jpo-geojsonconverter, jpo-ode-*, and jpo-conflictmonitor dependencies to match the version number of the corresponding releases. (e.g. 1.0.0)
        - [ ] Commit these changes to the `release/(year)-(quarter)` branch & push the changes to the remote repository.
        - [ ] Ensure these changes pass CI/CD checks before continuing.
    - [ ] Merge `release/(year)-(quarter)` branch into `master` branch for the jpo-conflictvisualizer project, and create a release with the version number of the release. (e.g. jpo-conflictvisualizer-x.x.x)
    - [ ] Create docker images
        - [ ] Use the release for the jpo-conflictmonitor to produce docker images containing the version number.
            - [ ] One image will be for the API
            - [ ] One image will be for Keycloak
        - [ ] Upload docker images to [DockerHub](https://hub.docker.com/u/usdotjpoode)
        - [ ] Tag docker images with the version number of the app. (e.g. 1.0.0)
        - [ ] Tag docker images with year and quarter of release. (e.g. 2024-Q2)
        - [ ] Tag docker images with 'latest' tag for the most recent release.
    - [ ] Merge master branch into develop branch & verify that CI/CD passes.

### 4. DockerHub Image Testing
    - [ ] jpo-conflictvisualizer-api
        - [ ] image starts up correctly
        - [ ] GUI functions & can display messages
    - [ ] jpo-conflictvisualizer-keycloak
        - [ ] image starts up correctly
        - [ ] authentication verified to work


## jpo-deduplicator
### Prerequisites
    - [ ] jpo-ode release completed
    - [ ] jpo-geojsonconverter release completed
    - [ ] jpo-conflictmonitor release completed

#### Dependency Types
| Dependency | Type |
| --- | --- |
| jpo-ode | Maven |
| jpo-geojsonconverter | Maven |
| jpo-conflictmonitor | Maven |

### 1. Code Ready & Release Notes
    - [ ] Release notes drafted & added to `Release_notes.md` file in `docs` directory
    - [ ] Code changes for release are merged into `develop`
    - [ ] A new branch `release/(year)-(quarter)` is created from `develop`

### 2. Preliminary Testing
    - [ ] deduplicator code compiles
    - [ ] deduplicator unit tests pass
    - [ ] program starts up correctly
    - [ ] program processes BSM, SPaT, Map, and TIM messages
    - [ ] ProcessedMap, ProcessedSpat, OdeMap, OdeTim, OdeRawEncodedTim, OdeBsm messages are consumed
    - [ ] A single message is output for each of the input types

### 3. Project Reference Updates & Release Creation
    - [ ] Update version number in pom.xml file for the `jpo-deduplicator` project if not already done.
    - [ ] Update the corresponding version number for the jpo-geojsonconverter and jpo-ode-* dependencies in the pom.xml files of the jpo-deduplicator project. This change will be necessary in the jpo-deduplicator/pom.xml file.
    - [ ] Update git submodule references for the ‘jpo-ode’ project to point to tagged commits in projects with updated `master` branches.
        - [ ] Open the jpo-ode project in an IDE.
        - [ ] Navigate to the jpo-utils directory and run `git checkout tags/jpo-utils-x.x.x` to update the submodule reference.
    - [ ] Merge `release/(year)-(quarter)` branch into `master` branch for the jpo-deduplicator project, update the existing release tag (e.g. jpo-deduplicator-x.x.x) to point to the newly committed version of jpo-deduplicator
    - [ ] Create docker image
        - [ ] Use the release for the jpo-deduplicator to produce docker image with the same tag name, containing the version number.
        - [ ] Upload docker image to [DockerHub](https://hub.docker.com/u/usdotjpoode)
        - [ ] Tag docker image with the version number of the app. (e.g. 1.0.0)
        - [ ] Tag docker image with year and quarter of release. (e.g. 2024-Q2)
        - [ ] Tag docker image with 'latest' tag for the most recent release.
    - [ ] Merge master branch into develop branch & verify that CI/CD passes.

### 4. DockerHub Image Testing
    - [ ] image starts up correctly
    - [ ] ProcessedMap, ProcessedSpat, OdeMap, OdeTim, OdeRawEncodedTim, OdeBsm messages are consumed
    - [ ] A single message is output for each of the input types


## jpo-cvmanager
### Prerequisites
    - [ ] jpo-ode release completed
    - [ ] jpo-conflictmonitor release completed
    - [ ] jpo-geojsonconverter release completed
    - [ ] asn1_codec release completed

#### Dependency Types
| Dependency | Type |
| --- | --- |
| jpo-ode | Maven |
| jpo-geojsonconverter | Maven |
| jpo-conflictmonitor | Maven |
| asn1_codec | Git Submodule |

### 1. Code Ready & Release Notes
    - [ ] Release notes drafted & added to `Release_notes.md` file in `docs` directory
    - [ ] Code changes for release are merged into `develop`
    - [ ] A new branch `release/(year)-(quarter)` is created from `develop`

### 2. Preliminary Testing
    - [ ] code compiles
    - [ ] unit tests pass
        - [ ] python api
        - [ ] webapp
        - [ ] intersection api
    - [ ] program starts up correctly
    - [ ] webapp can be signed into successfully
    - [ ] map page displays RSUs status information, RSU filtering, and displaying message counts in side window + heatmap
    - [ ] map page displays work zones (wzdx), intersection locations, and supports historic BSM queries
    - [ ] intersection map displays live and historic data
    - [ ] intersection dashboard displays notifications and assessments

### 3. Project Reference Updates & Release Creation
    - [ ] Merge `release/(year)-(quarter)` branch into `master` branch for the jpo-cvmanager project, and create a release with the version number of the release. (e.g. jpo-cvmanager-x.x.x)
    - [ ] Merge master branch into develop branch & verify that CI/CD passes.

## jpo-s3-deposit
### Prerequisites
None

### 1. Code Ready & Release Notes
    - [ ] Release notes drafted & added to `Release_notes.md` file in `docs` directory
    - [ ] Code changes for release are merged into `develop`
    - [ ] A new branch `release/(year)-(quarter)` is created from `develop`

### 2. Preliminary Testing
    - [ ] code compiles
    - [ ] program starts up correctly
    - [ ] deposits can be made to one of the destinations successfully

### 3. Project Reference Updates & Release Creation
    - [ ] Update version number in pom.xml if not already done
    - [ ] Merge ‘release/(year)-(quarter)’ branch into ‘master’ branch for the jpo-s3-deposit project
    - [ ] Create a release for the jpo-s3-deposit project from the ‘master’ branch and tag the release with the version number of the release. (e.g. jpo-s3-deposit-x.x.x)
    - [ ] Create docker image
        - [ ] Use the release for the jpo-s3-deposit to produce docker image with the same tag name, containing the version number.
        - [ ] Upload docker image to [DockerHub](https://hub.docker.com/u/usdotjpoode)
        - [ ] Tag docker image with the version number of the app. (e.g. 1.0.0)
        - [ ] Tag docker image with year and quarter of release. (e.g. 2024-Q2)
        - [ ] Tag docker image with 'latest' tag for the most recent release.
    - [ ] Merge master branch into develop branch & verify that CI/CD passes.

### 4. DockerHub Image Testing
    - [ ] image starts up correctly
    - [ ] deposits can be made to one of the destinations successfully


...

At this point the quarterly release process is complete.