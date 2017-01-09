# Kafka Consumer Example

This project is intended to serve as a sample consumer application to subscribe to a Kafka topic of streams. After the ODE is up and running, this example project will showcase how you can build a Java application to subscribe to the outputs of a Kafka Stream.

## Quick Install


The sample project needs to be compiled with assembly to ensure that that resulting jar is runnable with the Kafka libraries. It will produce a jar under `target/` with a "with-dependencies" tag.

```mvn clean compile assembly:single install```

To run the jar, be sure to include the topic at the end and group id at the end. If this is not a distributed system, the group can be any string.

```java -jar target/consumer-example-1.0-SNAPSHOT-jar-with-dependencies.jar  <topic> <group>```



