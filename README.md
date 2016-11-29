# jpo-ode
US Department of Transportation Joint Program office (JPO) Operational Data Environment (ODE)
## Project Description
In the context of ITS, an Operational Data Environment is a real-time data acquisition and distribution software system that processes and routes data from Connected-X devices –including connected vehicles (CV), personal mobile devices, and infrastructure components and sensors –to subscribing applications to support the operation, maintenance, and use of the transportation system, as well as related research and development efforts.

## Project Goals
 
## Release Notes
### Release 1.0

## Collaboration Tools

### Source Repository - GitHub
https://github.com/usdot-jpo-ode/jpo-ode

### Agile Project Management - Taiga
https://tree.taiga.io/project/toryb-its_jpo_ode_agile/

### Wiki - Taiga
https://tree.taiga.io/project/toryb-its_jpo_ode_agile/wiki/home

### Continuous Integration and Delivery
https://travis-ci.org/usdot-jpo-ode/jpo-ode

## Getting Started

### Local Build

###### Dependencies

You will need the following dependencies to run the application:

* Maven: [https://maven.apache.org/install.html](https://maven.apache.org/install.html)

IDE of your choice:

* IntelliJ: [https://www.jetbrains.com/idea/](https://www.jetbrains.com/idea/)
* STS: [https://spring.io/tools/sts/all](https://spring.io/tools/sts/all)
* Eclipse: [https://eclipse.org/](https://eclipse.org/)

Private Jars:

* ASN.1 BSM Encoder/Decoder: To receive the private jar, please contact the development team.


#### Clone the Repo

Please request access if needed, but with a git client or the command line, you can clone repo.

`git clone https://github.com/usdot-jpo-ode/jpo-ode.git`

#### Building and Running the project

To build the project using maven command line:

Navigate to the root directory

`cd jpo-ode/`

Build the project, downloading all of the JAR needed and compiling the code

`mvn clean install`


Navigate to the project folder supporting the service

`cd /jpo-ode-svcs`

Run the script to establish a local Spring instance

`sh run.sh`

You should be able to access the running service at `localhost:8080`, but submissions will not work without the ASN.1 Jar installed locally.

Once you have a jar installed with the project, you should be able to upload the following file and test for a successful output. 

```
{
	"coreData": {
		"position":	{
				"latitude":42.3288028,
				"longitude":-83.048916,
				"elevation":157.5
		}
	}
}
```

And the output:

```
2016-11-29 10:52:13.793  INFO 6449 --- [nio-8080-exec-2] u.d.i.j.o.s.FileSystemStorageService     : AExMjM0AAFrSdJU1pOjWCE6AAAAAAAUAAH59B9B/f/8AAAUAUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==

2016-11-29 10:52:13.794  INFO 6449 --- [nio-8080-exec-2] u.d.i.j.o.s.FileSystemStorageService     : Latitude: 0.0000042

2016-11-29 10:52:13.794  INFO 6449 --- [nio-8080-exec-2] u.d.i.j.o.s.FileSystemStorageService     : Longitude: -0.0000083

2016-11-29 10:52:13.795  INFO 6449 --- [nio-8080-exec-2] u.d.i.j.o.s.FileSystemStorageService     : Elevation: 15.7
```

Which demonstrates a loop of Readable JSON -> ASN.1 UPER encoded BSM Message -> Readable Output



### Continuous Integration and Delivery

### Deployment

## Docker
docker/README.md
