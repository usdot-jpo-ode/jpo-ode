package us.dot.its.jpo.ode.wrapper;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

import kafka.serializer.Decoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MQDecoder<T> implements Decoder<T> {
   private static Logger logger = LoggerFactory.getLogger(MQDecoder.class);
   
   @SuppressWarnings("unchecked")
   @Override
   public T fromBytes(byte[] data) {
      T ddsData = null;
      try {
         ByteArrayInputStream in = new ByteArrayInputStream(data);
         ObjectInputStream is;
         is = new ObjectInputStream(in);
         return (T) is.readObject();
      } catch (Exception e) {
         logger.error("Error decoding data.", e);
      }
      return ddsData;
   }

}
