package com.consumerexample.app;

import java.util.Properties;
import java.util.Arrays;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.commons.cli.*;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.util.SerializationUtils;

public class ConsumerExample {
	
	public static void main( String[] args ){
		
		// Option parsing
		Options options = new Options();
		
		Option bootstrap_server = new Option("b", "bootstrap-server", true, "Endpoint ('ip:port')");
		bootstrap_server.setRequired(true);
		options.addOption(bootstrap_server);

		Option topic_option = new Option("t", "topic", true, "Topic Name");
		topic_option.setRequired(true);
		options.addOption(topic_option);

		Option group_option = new Option("g", "group", true, "Consumer Group");
		group_option.setRequired(true);
		options.addOption(group_option);

		Option type_option = new Option("type", "type", true, "string|byte message type");
		type_option.setRequired(true);
		options.addOption(type_option);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;

		 try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("Consumer Example", options);

			System.exit(1);
			return;
		}

		String endpoint = cmd.getOptionValue("bootstrap-server");
		String topic = cmd.getOptionValue("topic");
		String group = cmd.getOptionValue("group");
		String type = cmd.getOptionValue("type");

		// Properties for the kafka topic
		Properties props = new Properties();
		props.put("bootstrap.servers", endpoint);
		props.put("group.id", group);
		props.put("enable.auto.commit", "false");
		props.put("auto.commit.interval.ms", "1000");
		props.put("session.timeout.ms", "30000");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		if (type.equals("byte")){ 
			props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
		} else {
			props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		}

		if (type.equals("byte")) {
			KafkaConsumer<String, byte[]> byteArrayConsumer = new KafkaConsumer<String, byte[]>(props);

			byteArrayConsumer.subscribe(Arrays.asList(topic));
			System.out.println("Subscribed to topic " + topic);
			while (true) {
				ConsumerRecords<String, byte[]> records = byteArrayConsumer.poll(100);
				for (ConsumerRecord<String, byte[]> record : records) {
					// Serialize the record value
					SerializationUtils<J2735Bsm> serializer = new SerializationUtils<J2735Bsm>();
					J2735Bsm bsm =  serializer.deserialize(record.value());
					System.out.print(bsm.toString()); 
				}
			}
		} else {
			KafkaConsumer<String, String> stringConsumer = new KafkaConsumer<String, String>(props);

			stringConsumer.subscribe(Arrays.asList(topic));
			System.out.println("Subscribed to topic " + topic);
			while (true) {		
				ConsumerRecords<String, String> records = stringConsumer.poll(100);
				for (ConsumerRecord<String, String> record : records) {
					System.out.print(record.value()); 
				}
			}
		
		}
	}
}
