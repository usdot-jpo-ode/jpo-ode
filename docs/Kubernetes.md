# Running the ODE in K8s
## Intro to Kubernetes
Kubernetes, also known as K8s, is a container orchestration tool that allows for the deployment of services and ensures their uptime through high availability, scalability, and disaster recovery.
With this tool, there is less to worry about concerning the management of containers.
Since the ODE is needs to be always available, it makes sense to use K8s to ensure as little downtime as possible.
Further, since K8s is scalable, it will be easier to expand services as the demand for access to the ODE increases.

## K8s Management
Several technologies exist which allow for the abstraction of the management of the environment that the K8s clusters will be living on.
They include Google Kubernetes Engine (GKE), AWS Elastic Container Service for Kubernetes (Amazon EKS), and OpenShift.
The CDOT project will be using GKE to manage its environment.

## CDOT Implementation
CDOT is expanding on the existing ODE deployments to run within a Kubernetes environment.

### Helm
To spin up the clusters, Helm will be utilized, which will manage all of our YAML files as a unit.
With just Kubernetes, one has to run a command every time they want to deploy a YAML file.
With Helm, all the YAML files can be deployed with just one command.

#### Helm Commands
- helm install (name) (name) -n (namespace) -f (values file)
- helm upgrade --install (name) (name) -n (namespace) -f (values file)

### Autopilot
The CDOT project is also using clusters in autopilot mode, which means that resources will be allocated based on necessity.
This will make it easier to scale up the deployments and will result in less overhead.

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
```
{{- range .Values.acm }}
---
apiVersion: "apps/v1"
kind: "Deployment"
metadata:
  name: {{ .NAME | quote }}
spec:
  replicas: {{ $.Values.replicas.jpoode_acm }}
  selector:
    matchLabels:
      app: {{ .NAME | quote }}
  template:
    metadata:
      labels:
        app: {{ .NAME | quote }}
    spec:
      # affinity:
      #   podAntiAffinity:
      #     requiredDuringSchedulingIgnoredDuringExecution:
      #       - labelSelector:
      #           matchExpressions:
      #             - key: "app"
      #               operator: In
      #               values:
      #               - {{ .NAME }}
      #         topologyKey: "kubernetes.io/hostname"
      containers:
        - name: "jpoode-acm-image-sha256-1"
          image: "{{ $.Values.images.jpoode_acm.repository }}:{{ $.Values.images.jpoode_acm.tag }}"
          resources:
            requests:
              memory: "1Gi"
              cpu: "0.5"
          tty: true
          stdin: true
          env:
          - name: ACM_CONFIG_FILE
            value: {{ .CONFIG_FILE }}
          - name: DOCKER_HOST_IP
            value: {{ $.Values.resources.services.kafka }}
          volumeMounts:
            - mountPath: /asn1_codec_share
              name: {{ .NAME }}-storage
      volumes:
        - name: {{ .NAME }}-storage
{{- end }}
```

### Kafka
```
---
apiVersion: v1
kind: Service
metadata:
  name: {{ .Values.resources.services.kafka }}
  labels:
    app: jpoode-kafka
spec:
  ports:
    - port: 9092
      name: ode-kafkaserver
  clusterIP: None
  selector:
    app: jpoode-kafka
---
# limits the number of pods that are down simultaneously
apiVersion: policy/v1beta1
kind: PodDisruptionBudget
metadata:
  name: ode-kafka-pdb
spec:
  selector:
    matchLabels:
      app: jpoode-kafka
  maxUnavailable: 1
---
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: jpoode-kafka
spec:
  selector:
    matchLabels:
      app: jpoode-kafka
  serviceName: {{ .Values.resources.services.kafka }}
  replicas: {{ .Values.replicas.jpoode_kafka }}
  podManagementPolicy: Parallel
  updateStrategy:
    type: RollingUpdate
  template:
    metadata:
      labels:
        app: jpoode-kafka
    spec:
      terminationGracePeriodSeconds: 300
      containers:
        - name: k8skafka
          imagePullPolicy: Always
          image: confluentinc/cp-kafka:6.2.0
          resources:
            requests:
              memory: "1Gi"
              cpu: "0.5"
          ports:
            - containerPort: 9092
              name: ode-kafkaserver
          env:
            - name: POD_IP
              valueFrom:
                fieldRef:
                  fieldPath: status.podIP
            - name: HOST_IP
              valueFrom:
                fieldRef:
                  fieldPath: status.hostIP
            - name: POD_NAME
              valueFrom:
                fieldRef:
                  fieldPath: metadata.name
            - name: SERVICE_NAME
              value: {{ .Values.resources.services.kafka }}
            - name: POD_NAMESPACE
              valueFrom:
                fieldRef:
                  fieldPath: metadata.namespace
            - name: KAFKA_HEAP_OPTS
              value: "-Xms512M -Xmx512M"
            - name: KAFKA_ZOOKEEPER_CONNECT
              value: {{ .Values.resources.services.zookeeper }}
            - name: KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR
              value: "1"
            - name: KAFKA_NUM_PARTITIONS
              value: "{{ .Values.replicas.jpoode_kafka }}"
            # This is required because the Downward API does not yet support identification of
            # pod numbering in statefulsets. Thus, we are required to specify a command which
            # allows us to extract the pod ID for usage as the Kafka Broker ID.
            # See: https://github.com/kubernetes/kubernetes/issues/31218
          command:
          - sh
          - -exc
          - |
            export KAFKA_BROKER_ID=${HOSTNAME##*-} && \
            export KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://${POD_NAME}.${SERVICE_NAME}.${POD_NAMESPACE}:9092 && \
            exec /etc/confluent/docker/run
          volumeMounts:
            - name: datadir
              mountPath: /var/lib/kafka
      securityContext:
        runAsUser: 1000
        fsGroup: 1000
  volumeClaimTemplates:
    - metadata:
        name: datadir
      spec:
        accessModes: ["ReadWriteOnce"]
        resources:
          requests:
            storage: 10Gi
```

### ODE
```
---
apiVersion: v1
kind: Service
metadata:
  name: jpoode-ode-tcp-svc
  labels:
    app: jpoode-ode
spec:
  type: LoadBalancer 
  ports:
  - port: 8080
    name: ode-webserver 
  loadBalancerSourceRanges:
    {{- range .Values.config.ode.sourceRanges }}
    - {{ . | title }}
    {{- end }}
  selector:
    app: jpoode-ode
---
apiVersion: v1
kind: Service
metadata:
  name: jpoode-ode-udp-svc
  annotations:
    networking.gke.io/load-balancer-type: "Internal"
  labels:
    app: jpoode-ode
spec:
  type: LoadBalancer
  loadBalancerIP: {{ .Values.config.ode.udp_service.ipAddress }}
  ports:
  - name: ode-rsu-bsm
    protocol: UDP
    port: 46800
  - name: ode-rsu-tim
    protocol: UDP
    port: 47900
  externalTrafficPolicy: Local
  loadBalancerSourceRanges:
    {{- range .Values.config.ode.sourceRanges }}
    - {{ . | title }}
    {{- end }}
  selector:
    app: jpoode-ode
---
{{- if .Values.config.ode.udp_service.exposeExternally }}
apiVersion: v1
kind: Service
metadata:
  name: jpoode-ode-udp-svc-ext
  labels:
    app: jpoode-ode
spec:
  type: LoadBalancer
  ports:
  - name: ode-rsu-bsm
    protocol: UDP
    # targetPort: 46800
    port: 46801
  - name: ode-rsu-tim
    protocol: UDP
    # targetPort: 47900
    port: 47901
  externalTrafficPolicy: Local
  loadBalancerSourceRanges:
    {{- range .Values.config.ode.sourceRanges }}
    - {{ . | title }}
    {{- end }}
  selector:
    app: jpoode-ode
---
{{- end }}
apiVersion: "apps/v1"
kind: "Deployment"
metadata:
  name: "jpoode-ode"
  labels:
    app: "jpoode-ode"
spec:
  replicas: {{ .Values.replicas.jpoode_ode }}
  selector:
    matchLabels:
      app: "jpoode-ode"
  template:
    metadata:
      labels:
        app: "jpoode-ode"
    spec:
      # affinity:
      #   podAntiAffinity:
      #     requiredDuringSchedulingIgnoredDuringExecution:
      #       - labelSelector:
      #           matchExpressions:
      #             - key: "app"
      #               operator: In
      #               values:
      #               - jpoode-ode
      #         topologyKey: "kubernetes.io/hostname"
      #   podAffinity:
      #     preferredDuringSchedulingIgnoredDuringExecution:
      #        - weight: 1
      #          podAffinityTerm:
      #            labelSelector:
      #               matchExpressions:
      #                 - key: "app"
      #                   operator: In
      #                   values:
      #                   - jpoode-kafka
      #            topologyKey: "kubernetes.io/hostname"
      containers:
        - name: "jpoode-ode-image-sha256-1"
          image: "{{ .Values.images.jpoode_ode.repository }}:{{ .Values.images.jpoode_ode.tag }}" 
          resources:
            requests:
              memory: "1.5Gi"
              cpu: "1"
          tty: true
          stdin: true
          ports:
          - containerPort: 8080
          - containerPort: 9090
          - containerPort: 46800
            protocol: UDP
          - containerPort: 47900
            protocol: UDP
            {{- if .Values.config.ode.udp_service.exposeExternally }}
          - containerPort: 46801
            protocol: UDP
          - containerPort: 47901
            protocol: UDP
            {{- end }}
          env:
          - name: DOCKER_HOST_IP
            valueFrom:
              fieldRef:
                fieldPath: status.podIP
          - name: ODE_KAFKA_BROKERS
            value: {{ .Values.resources.services.kafka }}:9092
          - name: ZK
            value: {{ .Values.resources.services.zookeeper }}:2181
          - name: ODE_SECURITY_SVCS_SIGNATURE_URI
            value: {{ .Values.resources.services.security }}:8090
          - name: ODE_RSU_USERNAME
            valueFrom: 
              secretKeyRef:
                name: jpoode-secrets
                key: ode_rsu_username
          - name: ODE_RSU_PASSWORD
            valueFrom:
              secretKeyRef:
                name: jpoode-secrets
                key: ode_rsu_password
          volumeMounts:
            - mountPath: /jpo-ode
              name: jpoode-ode-storage 
      volumes:
        - name: jpoode-ode-storage
```

### PPM
```
{{- range .Values.ppm }}
---
apiVersion: "apps/v1"
kind: "Deployment"
metadata:
  name: {{ .NAME | quote }}
spec:
  replicas: {{ $.Values.replicas.jpoode_ppm }}
  selector:
    matchLabels:
      app: {{ .NAME | quote }}
  template:
    metadata:
      labels:
        app: {{ .NAME | quote }}
    spec:
      containers:
        - name: "jpoode-ppm-image-sha256-1"
          imagePullPolicy: Always
          image: "{{ $.Values.images.jpoode_ppm.repository }}:{{ $.Values.images.jpoode_ppm.tag }}"
          resources:
            requests:
              memory: "1Gi"
              cpu: "0.5"
          tty: true
          stdin: true
          env:
          - name: PPM_CONFIG_FILE
            value: {{ .PPM_CONFIG_FILE }}
          - name: DOCKER_HOST_IP
            value: {{ $.Values.resources.services.kafka }}
{{- end }}          
```

### SEC
```
---
apiVersion: v1
kind: Service
metadata:
  name: "jpoode-sec-svc"
  labels:
    app: "jpoode-sec"
spec:
  ports:
  - port: 8090
    name: "ode-sec-server" 
  clusterIP: "None" 
  selector:
    app: "jpoode-sec"
---
apiVersion: "apps/v1"
kind: "Deployment"
metadata:
  name: "jpoode-sec"
  labels:
    app: "jpoode-sec"
spec:
  replicas: {{ .Values.replicas.jpoode_sec }}
  selector:
    matchLabels:
      app: "jpoode-sec"
  template:
    metadata:
      labels:
        app: "jpoode-sec"
    spec:
      containers:
      - name: "jpoode-sec-image-sha256-1"
        image: "{{ .Values.images.jpoode_sec.repository }}:{{ .Values.images.jpoode_sec.tag }}" 
        resources:
          requests:
            memory: "1Gi"
            cpu: "0.25"
        tty: true
        stdin: true
```

### Zookeeper
```
---
apiVersion: v1
kind: Service
metadata:
  name: {{ .Values.resources.services.zookeeper }} 
  labels:
    app: jpoode-zookeeper
spec:
  ports:
  - port: 2181
    name: ode-zookeeper-client
  - port: 2888
    name: ode-zookeeper-server 
  - port: 3888
    name: ode-zookeeper-leader-election
  clusterIP: None 
  selector:
    app: jpoode-zookeeper
---
apiVersion: policy/v1beta1
kind: PodDisruptionBudget
metadata: 
  name: ode-zookeeper-pdb
spec:
  selector:
    matchLabels:
      app: jpoode-zookeeper 
  maxUnavailable: 1
---
apiVersion: apps/v1
kind: StatefulSet
metadata: 
  name: jpoode-zookeeper
spec:
  selector:
    matchLabels:
      app: jpoode-zookeeper
  serviceName: {{ .Values.resources.services.zookeeper }} 
  replicas: {{ .Values.replicas.jpoode_zookeeper }}
  podManagementPolicy: Parallel
  updateStrategy:
    type: RollingUpdate
  template:
    metadata:
      labels:
        app: jpoode-zookeeper
    spec:
      # assign pods to nodes
      affinity:
        podAntiAffinity:
          requiredDuringSchedulingIgnoredDuringExecution:
            - labelSelector:
                matchExpressions:
                  - key: "app"
                    operator: In
                    values:
                    - jpoode-zookeeper
              topologyKey: "kubernetes.io/region"
      containers:
      - name: kubernetes-zookeeper
        imagePullPolicy: Always
        image: confluentinc/cp-zookeeper:6.2.0
        resources:
          requests:
            memory: "1Gi"
            cpu: "0.5"
        tty: true
        stdin: true
        ports:
          - containerPort: 2181
            name: ode-zk-client
          - containerPort: 2888
            name: ode-zk-server
          - containerPort: 3888
            name: ode-zk-election
        env:
          - name: ZOOKEEPER_CLIENT_PORT
            value: "2181"
          - name: ZOOKEEPER_TICK_TIME
            value: "2000"
          - name : ZOOKEEPER_SYNC_LIMIT
            value: "5"
          - name : ZOOKEEPER_INIT_LIMIT
            value: "10"
          - name : ZOOKEEPER_MAX_CLIENT_CNXNS
            value: "60"
          - name : ZOOKEEPER_AUTOPURGE_SNAP_RETAIN_COUNT
            value: "3"
          - name : ZOOKEEPER_AUTOPURGE_PURGE_INTERVAL
            value: "24"
          # - name: ZOOKEEPER_SERVERS
          #   value: "{{ .Values.replicas.jpoode_zookeeper }}"
          - name: ZOOKEEPER_DATA_DIR
            value: "/var/lib/zookeeper/data"
          - name: ZOOKEEPER_DATA_LOG_DIR
            value: "/var/lib/zookeeper/data/log"
          - name: ZOOKEEPER_ELECTION_PORT
            value: "3888"
          - name: ZOOKEEPER_SERVER_PORT
            value: "2888"
          - name: ZOOKEEPER_HEAP
            value: "512M"
          - name: ZOOKEEPER_LOG_LEVEL
            value: "INFO"
          - name: ZOOKEEPER_CONF_DIR
            value: "/opt/zookeeper/conf"
        command:
          - /bin/bash
          - -c
          - export ZOOKEEPER_SERVER_ID=$((${HOSTNAME##*-}+1)) && /etc/confluent/docker/run
        volumeMounts:
        - name: datadir
          mountPath: /var/lib/zookeeper
      securityContext:
        runAsUser: 1000
        fsGroup: 1000
  volumeClaimTemplates:
  - metadata:
      name: datadir
    spec:
      accessModes: [ "ReadWriteOnce" ]
      resources:
        requests:
          storage: 10Gi
---
```

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