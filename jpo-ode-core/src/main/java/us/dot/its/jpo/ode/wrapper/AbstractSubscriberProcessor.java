package us.dot.its.jpo.ode.wrapper;

import java.util.Arrays;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 572682
 *
 *         This abstract class provides a basic service that subscribes to one
 *         or more topics and process the received messages in the derived
 *         classes.
 * 
 * @param <K>
 *           Message Key type
 * @param <V>
 *           Received Message Value Type
 */
public abstract class AbstractSubscriberProcessor<K, V> extends MessageProcessor<K, V> {

   protected Logger logger = LoggerFactory.getLogger(this.getClass());
   protected int messagesConsumed = 0;

   /**
    * Starts a Kafka listener that runs call() every time a new msg arrives
    * 
    * @param consumer
    * @param inputTopics
    */
   public void start(MessageConsumer<K, V> consumer, String... inputTopics) {
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

      V consumedData = getRecord().value();

      return process(consumedData);
   }

   protected abstract Object process(V consumedData);

}
