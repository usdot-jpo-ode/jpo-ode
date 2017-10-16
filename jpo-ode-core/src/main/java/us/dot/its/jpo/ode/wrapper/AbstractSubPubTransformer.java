package us.dot.its.jpo.ode.wrapper;

import java.util.Arrays;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 572682
 *
 * This abstract class provides a basic pipeline functionality through the messaging
 * framework. The objects of this class subscribe to a topic, process received messages
 * and publish the results to another topic.
 *   
 * @param <K> Message Key type
 * @param <V1> Received Message Value Type
 * @param <V2> Published Message Value Type
 */
public abstract class AbstractSubPubTransformer<K, V1, V2> extends MessageProcessor<K, V1> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    protected int messagesConsumed = 0;
    protected int messagesPublished = 0;
    protected MessageProducer<K, V2> producer;
    protected String outputTopic;

    public AbstractSubPubTransformer(MessageProducer<K, V2> producer, String outputTopic) {
        this.producer = producer;
        this.outputTopic = outputTopic;
    }

    /**
     * Starts a Kafka listener that runs call() every time a new msg arrives
     * 
     * @param consumer
     * @param inputTopics
     */
    public void start(MessageConsumer<K, V1> consumer, String... inputTopics) {
        logger.info("Subscribing to {}", Arrays.asList(inputTopics).toString());
        
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                consumer.subscribe(inputTopics);
            }
        });
    }

    @Override
    public Object call() {
        messagesConsumed++;
        
        V1 consumedData = record.value();
        
        V2 toBePublished = transform(consumedData);

        if (null != toBePublished) {
           producer.send(outputTopic, record.key(), toBePublished);
        }
        
        return consumedData;
    }

    protected abstract V2 transform(V1 consumedData);

}
