package us.dot.its.jpo.ode.wrapper;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.processor.ProcessorSupplier;

public class MessagePipe {
	private String srcName;
	private String[] srcTopics;
	private String sinkName;
	private String sinkTopic;
	private String[] parentNames;
	private String procName;
	private ProcessorSupplier<?, ?> procSupplier;
	private String procParentNames;
	
	void start() {
		Map<String, Object> props = new HashMap<String, Object>();
	    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "my-stream-processing-application");
	    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
	    props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, StringSerializer.class);
	    props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, StringSerializer.class);
	    StreamsConfig config = new StreamsConfig(props);

	    KStreamBuilder builder = new KStreamBuilder();
	    builder
	    	.addSource(srcName, srcTopics)
	    	.addSink(sinkName, sinkTopic, parentNames)
	    	.addProcessor(procName, procSupplier, procParentNames);
	    

	    KafkaStreams streams = new KafkaStreams(builder, config);
	    streams.start();
	 
	}

}
