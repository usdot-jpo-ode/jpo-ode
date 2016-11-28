# jpo-ode
US Department of Transportation Joint Program office (JPO) Operational Data Environment (ODE)

## Background


The SEMI ODE is intended to complement a connected vehicle infrastructure by functioning as a smart data router by brokering processed data from various data sources, including connected vehicles, to a variety of data users. Data users include transportation software applications, such as those that may be used by a Transportation Management Center (TMC).
As a data provisioning service, the SEMI ODE can provision data from disparate data sources to software applications that have placed data subscription requests to the SEMI ODE. These subscribing applications may include CV applications as well as non-CV applications. While provisioning data from data sources for data users, the SEMI ODE also performs necessary security / credential checks and, as needed, data valuation, aggregation, integration, sanitization and propagation functions. These functions are core functions to the SEMI ODE and are detailed in later sections. However, these may be summarized, in no particular order, as the following:
1.3
   
   
* data valuation is the process of making a judgment about the quality or value of the data
* data integration is the process of combining different data from multiple sources to provide more complete information
* data sanitization is the modification of data as originally received in order to reduce or eliminate the possibility that the data can be used to compromise the privacy of the individual(s) that might be linked to the data
* data aggregation is the creation of composite or summary information from more granular data

## Dependencies

You will need the following dependencies to run the application:

* Maven: [https://maven.apache.org/install.html](https://maven.apache.org/install.html)

IDE of your choice:

* IntelliJ: [https://www.jetbrains.com/idea/](https://www.jetbrains.com/idea/)
* STS: [https://spring.io/tools/sts/all](https://spring.io/tools/sts/all)
* Eclipse: [https://eclipse.org/](https://eclipse.org/)

## Clone the Repo

Please request access if needed, but with a git client or the command line, you can clone repo.

`git clone https://github.com/usdot-jpo-ode/jpo-ode.git`

## Building the project

To build the project using maven command line:

`cd jpo-ode/`

`mvn clean install`


