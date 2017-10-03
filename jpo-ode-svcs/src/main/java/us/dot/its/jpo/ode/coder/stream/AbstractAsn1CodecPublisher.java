package us.dot.its.jpo.ode.coder.stream;

import java.io.BufferedInputStream;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.coder.ByteArrayPublisher;

public abstract class AbstractAsn1CodecPublisher implements Asn1CodecPublisher {

   protected static final Logger logger = LoggerFactory.getLogger(AbstractAsn1CodecPublisher.class);

   protected ByteArrayPublisher publisher;

   protected static AtomicInteger bundleId = new AtomicInteger(1);

   public AbstractAsn1CodecPublisher(ByteArrayPublisher dataPub) {
      this.publisher = dataPub;
   }

   @Override
   public abstract void publish(BufferedInputStream is, String fileName) throws Exception;
}
