# Kafka Consumer Example

This project is intended to serve as a sample consumer application to subscribe to a Kafka topic of streams. After the ODE is up and running, this example project will showcase how you can build a Java application to subscribe to the outputs of a Kafka Stream.

## Quick Run


The sample project needs to be compiled with assembly to ensure that that resulting jar is runnable with the Kafka libraries. It will produce a jar under `target/` with a "with-dependencies" tag.

```
mvn clean compile assembly:single install
```

To run the jar, be sure to include the topic at the end and group id at the end. If this is not a distributed system, the group can be any string.

```
java -jar target/consumer-example-1.0-SNAPSHOT-jar-with-dependencies.jar     

usage: Consumer Example
 -b,--bootstrap-server <arg>   Endpoint ('ip:port')
 -g,--group <arg>              Consumer Group
 -t,--topic <arg>              Topic Name
 -type,--type <arg>            string|byte message type
```

Example Usage As Of: 1/11/17

``` 
java -jar target/consumer-example-1.0-SNAPSHOT-jar-with-dependencies.jar --bootstrap-server localhost:9092 -g group1 -t topic.J2735Bsm -type byte
```

It should return the following confirmation

```
Subscribed to topic topic.J2735Bsm
```
Triggering an upload into the ODE, the output should be seen decoded into JSON in the console.

![CLI-output](images/cli-output.png)

## Additional Resources

With the Kafka installed locally on a machine, here are a few additional commands that may be helpful while debugging Kafka topics.

[Kafka Install Instructions](https://www.cloudera.com/documentation/kafka/latest/topics/kafka_installing.html#concept_ngx_4l4_4r)

The IP used is the location of the Kafka endpoints.

####Create, alter, list, and describe topics.

```
kafka-topics --zookeeper 192.168.1.151:2181 --list
sink1
t1
t2
```

####Read data from a Kafka topic and write it to standard output. 

```
kafka-console-consumer --zookeeper 192.168.1.151:2181 --topic topic.J2735Bsm
```

####Read data from standard output and write it to a Kafka topic. 

```
kafka-console-producer --broker-list 192.168.1.151:9092 --topic topic.J2735Bsm 
```
