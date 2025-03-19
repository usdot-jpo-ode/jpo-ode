package us.dot.its.jpo.ode;

import java.time.Duration;
import java.time.Instant;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.apache.kafka.streams.state.WindowStore;
import org.apache.kafka.streams.state.WindowStoreIterator;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;


/**
 * The OdeTimJsonTopology class sets up and manages a Kafka Streams topology for processing TIM
 * (Traveler Information Message) JSON data from the OdeTimJson Kafka topic. This class creates a
 * K-Table that houses TMC-generated TIMs which can be queried by UUID.
 **/
@Slf4j
public class OdeTimJsonTopology {

  private final KafkaStreams streams;

  /**
   * Constructs an instance of OdeTimJsonTopology to set up and manage a Kafka Streams topology for
   * processing TIM JSON data.
   *
   * @param odeKafkaProps the properties containing Kafka configuration, including brokers and
   *                      optional Confluent-specific configuration for authentication.
   * @param topic         the Kafka topic from which TIM JSON data is consumed to build the
   *                      topology.
   */
  public OdeTimJsonTopology(OdeKafkaProperties odeKafkaProps, String topic) {

    Properties streamsProperties = new Properties();
    streamsProperties.put(StreamsConfig.APPLICATION_ID_CONFIG, "KeyedOdeTimJson");
    streamsProperties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, odeKafkaProps.getBrokers());
    streamsProperties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
    streamsProperties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
    streamsProperties.put(StreamsConfig.WINDOW_SIZE_MS_CONFIG, 3600 * 1000L); // 1 hour retention

    if ("CONFLUENT".equals(odeKafkaProps.getKafkaType())) {
      streamsProperties.putAll(odeKafkaProps.getConfluent().buildConfluentProperties());
    }
    streams = new KafkaStreams(buildTopology(topic), streamsProperties);
    streams.setStateListener((newState, oldState) ->
        log.info("Transitioning from {} to {}", oldState, newState)
    );
    streams.start();
  }

  public boolean isRunning() {
    return streams.state().equals(KafkaStreams.State.RUNNING);
  }

  /**
   * Builds a Kafka Streams topology for processing TIM JSON data.
   *
   * @param topic the Kafka topic from which TIM JSON data is consumed and used to build the
   *              topology.
   * @return the constructed Kafka Streams topology.
   */
  public Topology buildTopology(String topic) {
    StreamsBuilder builder = new StreamsBuilder();

    // Create a windowed store with a retention period of 1 hour
    KStream<String, String> timStream = builder.stream(topic);

    timStream.groupByKey()
        .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofHours(1))) // 1 hour window
        .reduce(
            (aggValue, newValue) -> newValue, // only keep latest value
            Materialized.<String, String, WindowStore<Bytes, byte[]>>as("timjson-windowed-store")
                .withRetention(Duration.ofHours(1))
                .withKeySerde(Serdes.String())
                .withValueSerde(Serdes.String()));

    return builder.build();
  }

  /**
   * Query the windowed store by a specified UUID.
   *
   * @param uuid The specified UUID to query for.
   **/
  public String query(String uuid) {
    ReadOnlyWindowStore<String, String> windowStore =
        streams.store(StoreQueryParameters.fromNameAndType("timjson-windowed-store", QueryableStoreTypes.windowStore()));

    Instant now = Instant.now();
    Instant start = now.minus(Duration.ofHours(1));

    try (WindowStoreIterator<String> iterator = windowStore.fetch(uuid, start, now)) {
      while (iterator.hasNext()) {
        var value = String.valueOf(iterator.next().value);
        if (value != null) {
          return value;
        }
      }
    }
    return null;
  }
}
