package us.dot.its.jpo.ode.wrapper;

import us.dot.its.jpo.ode.util.SerializableObjectPool;

public class MQSerialazableProducerPool 
   extends SerializableObjectPool<MQProducer<String, String>> {
   private static final long serialVersionUID = 322882189929664360L;
   
   private String brokers;

   private String producerType; 
   
   public MQSerialazableProducerPool(String brokers, String producerType) {
      this.brokers = brokers;
      this.producerType = producerType;
   }

   @Override
   protected MQProducer<String, String> create() {
      return new MQProducer<String, String>(brokers, producerType);
   }

   @Override
   public boolean validate(MQProducer<String, String> o) {
      return o.getProducer() != null;
   }

   @Override
   public void expire(MQProducer<String, String> o) {
      o.shutDown();
   }

}
