# Running the ODE in K8s
## Intro to Kubernetes
Kubernetes, also known as K8s, is a container orchestration tool that allows for the deployment of services and ensures their uptime through high availability, scalability, and disaster recovery.
Kubernetes effectively abstracts away the difficulties with managing individual containers, making production deployments much more simple.
Production deployments of the ODE are required to be consistently available.
Therefore, using K8s to ensure as little downtime as possible is a viable solution.
Additional benefits for use with the ODE include the scalability of K8s, allowing implementations to expand services as the demand for access to the ODE increases.

## K8s Management
Rather than force management of the k8s cluster on the developer, several technologies exist which allow for the abstraction of the management of the environment that the K8s clusters will be living on.
These technologies live in various cloud environments and allow for massively scalable systems to be built.
In some cases, greater control of resource allocation can be taken advantage of.
Some of the more common instances include Google Kubernetes Engine (GKE), AWS Elastic Container Service for Kubernetes (Amazon EKS), and OpenShift among others.

## Colorado Department Of Transportation Implementation
CDOT is expanding on the existing ODE deployments to run within a Kubernetes environment.
The existing implementation was on a single system.
An expanded implementation would be more capable of handling massive amounts of data traffic.
The CDOT project will be using GKE to manage its environment.

### Helm
To spin up the clusters, Helm will be utilized.
Helm is essentially a package manager for Kubernetes, the purpose of which is to package YAML files and make them easy to manage with simple commands.
The documentation for Helm can be found [here](https://helm.sh/docs/).
With just Kubernetes, one has to run a command every time they want to deploy a YAML file.
With Helm, all the YAML files can be deployed or updated with just one command.

#### Helm Commands
- helm install (name) (name) -n (namespace) -f (values file)
- helm upgrade --install (name) (name) -n (namespace) -f (values file)

## Helm Chart
```
apiVersion: v2
name: jpoode
description: ODE Kubernetes Cluster

type: application
version: 0.1.2
appVersion: 1.0.0
```

## YAML Files
### ACM Template
[TODO: add description]
<br>
[Link](./k8s-yaml-files/jpoode_acm_template.yaml)

### Kafka
[TODO: add description]
<br>
[Link](./k8s-yaml-files/jpoode_kafka.yaml)

### ODE
[TODO: add description]
<br>
[Link](./k8s-yaml-files/jpoode_ode.yaml)

### PPM
[TODO: add description]
<br>
[Link](./k8s-yaml-files/jpoode_ppm_template.yaml)

### SEC
[TODO: add description]
<br>
[Link](./k8s-yaml-files/jpoode_sec.yaml)

### Zookeeper
[TODO: add description]
<br>
[Link](./k8s-yaml-files/jpoode_zookeeper.yaml)

## Values File Example
```
# Declare variables to be passed into your templates.

#Project name variable is overridden by Terraform
project_name: (project name goes here)

images:
  jpoode_acm:
    repository: (repository goes here)
    tag: "dev"
  gcp_connector:
    repository: (repository goes here)
    tag: "oim-dev"
  jpoode_ode:
    repository: (repository goes here)
    tag: "dev"
  jpoode_sec:
    repository: (repository goes here)
    tag: "dev"
  jpode_tim_dedup:
    repository: (repository goes here)
    tag: "oim-dev"

resources:
  services:
    kafka: (kafka service name)
    zookeeper: (zookeeper service name)
    security: (security service name)

replicas:
  jpoode_acm: (number)
  jpoode_gcp_connector: (number)
  jpoode_kafka: (number)
  jpoode_ode: (number)
  jpoode_sec: (number)
  jpoode_zookeeper: (number)

# Uses jpoode_acm_template.yaml
acm:
  - NAME: (name goes here)
    CONFIG_FILE: adm.properties
  - NAME: (name goes here)
    CONFIG_FILE: aem.properties

config:
  kafka:
    KAFKA_HEAP_OPTS: -Xmx2G -Xms2G
    KAFKA_OPTS: -Dlogging.level=INFO
  adm:
    ADM_CONFIG_FILE: adm.properties
  aem:
    AEM_CONFIG_FILE: aem.properties
  ode:
    sourceRanges:
      - (Subnet)
      - (Subnet)
      - (Subnet)
      - (Subnet)
    udp_service:
      ipAddress: (IP goes here)
      exposeExternally: true
```