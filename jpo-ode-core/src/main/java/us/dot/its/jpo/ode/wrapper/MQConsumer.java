package us.dot.its.jpo.ode.wrapper;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import us.dot.its.jpo.ode.metrics.OdeMetrics;
import us.dot.its.jpo.ode.metrics.OdeMetrics.Meter;

public class MQConsumer<K, V, R> implements Callable<Object> {

   private static Logger logger = LoggerFactory.getLogger(MQConsumer.class);

   private KafkaStream<K, V> m_stream;
   private int m_threadNumber;
   private DataProcessor<V, R> processor;
   
   protected Meter meter;

   public MQConsumer(KafkaStream<K, V> a_stream, int a_threadNumber,
         DataProcessor<V, R> a_processor) {
      this.m_threadNumber = a_threadNumber;
      this.m_stream = a_stream;
      this.processor = a_processor;
      
      this.meter = OdeMetrics.getInstance().meter(
            MQConsumer.class.getSimpleName(), 
            processor.getClass().getSimpleName(), "meter");
   }

   @Override
   public Object call() throws Exception {
      ConsumerIterator<K, V> it = m_stream.iterator();
      
      while (it.hasNext()) {
         V msg = it.next().message();

         processor.process(msg);
         
         meter.mark();
      } // End of while loop

      logger.info("Shutting down Thread: " + m_threadNumber);
      
      return null;
   }
}
