package us.dot.its.jpo.ode.udp;

import java.util.Properties;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import us.dot.its.jpo.ode.OdeProperties;

@Controller
public class KStreamsController {
	
	private static Logger logger = LoggerFactory.getLogger(KStreamsController.class);
	
	@Autowired
	public KStreamsController(OdeProperties odeProps) {
		super();

		   Properties streamsConfiguration = new Properties();
		   streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, this.getClass().getSimpleName());
		   streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, odeProps.getKafkaBrokers());
//		   streamsConfiguration.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, odeProps.getZookeeperConnect());
		   streamsConfiguration.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		   streamsConfiguration.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

		   final Serde<String> stringSerde = Serdes.String();
		   final Serde<Long> longSerde = Serdes.Long();

		   KStreamBuilder builder = new KStreamBuilder();
		   KStream<String, String> textLines = builder.stream(stringSerde, stringSerde, "TextLinesTopic");
//		   KStream<String, Long> wordCounts = textLines
//		         .flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
//		         .map((key, word) -> new KeyValue<>(word, word))
//		         // Required in Kafka 0.10.0 to re-partition the data because we re-keyed the stream in the `map` step.
//		         // Upcoming Kafka 0.10.1 does this automatically for you (no need for `through`).
//		         .through("RekeyedIntermediateTopic")
//		         .countByKey("Counts")
//		         .toStream()
//		         ;
//		   wordCounts.to(stringSerde, longSerde, "WordsWithCountsTopic");

		   KafkaStreams streams = new KafkaStreams(builder, streamsConfiguration);
		   streams.start();

		   Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
		}

}
