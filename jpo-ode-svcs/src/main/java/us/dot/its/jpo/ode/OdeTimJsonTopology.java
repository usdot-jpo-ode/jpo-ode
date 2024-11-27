package us.dot.its.jpo.ode;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.Stores;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;

import java.util.Properties;


/**
 * The OdeTimJsonTopology class sets up and manages a Kafka Streams topology
 * for processing TIM (Traveler Information Message) JSON data from the OdeTimJson Kafka topic.
 * This class creates a K-Table that houses TMC-generated TIMs which can be queried by UUID.
 **/
@Slf4j
public class OdeTimJsonTopology {

    private final KafkaStreams streams;

    public OdeTimJsonTopology(OdeKafkaProperties odeKafkaProps, String topic) {

        Properties streamsProperties = new Properties();
        streamsProperties.put(StreamsConfig.APPLICATION_ID_CONFIG, "KeyedOdeTimJson");
        streamsProperties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, odeKafkaProps.getBrokers());
        streamsProperties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        streamsProperties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);

        if ("CONFLUENT".equals(odeKafkaProps.getKafkaType())) {
            streamsProperties.put("sasl.jaas.config", odeKafkaProps.getConfluent().getSaslJaasConfig());
        }
        streams = new KafkaStreams(buildTopology(topic), streamsProperties);
        streams.setStateListener((newState, oldState) ->
                log.info("Transitioning from {} to {}", oldState, newState)
        );
        streams.start();
    }

    public void stop() {
        log.info("Stopping Ode Tim Json Topology");
        streams.close();
    }

    public boolean isRunning() {
        return streams.state().isRunningOrRebalancing();
    }

    public Topology buildTopology(String topic) {
        StreamsBuilder builder = new StreamsBuilder();
        builder.table(topic, Materialized.<String, String>as(Stores.inMemoryKeyValueStore("timjson-store")));
        return builder.build();
    }

    public String query(String uuid) {
        return (String) streams.store(StoreQueryParameters.fromNameAndType("timjson-store", QueryableStoreTypes.keyValueStore())).get(uuid);
    }
}
