package us.dot.its.jpo.ode.coder.stream;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.coder.BsmDecoderHelper;
import us.dot.its.jpo.ode.coder.BsmMessagePublisher;
import us.dot.its.jpo.ode.coder.TimDecoderHelper;
import us.dot.its.jpo.ode.importer.parser.LogFileParser;
import us.dot.its.jpo.ode.model.SerialId;

public abstract class AbstractDecoderPublisher implements DecoderPublisher {

   protected static final Logger logger = LoggerFactory.getLogger(AbstractDecoderPublisher.class);

   protected SerialId serialId;

   protected BsmMessagePublisher publisher;

   protected BsmDecoderHelper bsmDecoder;
   protected TimDecoderHelper timDecoder;

   protected static AtomicInteger bundleId = new AtomicInteger(1);

   public AbstractDecoderPublisher(
       BsmMessagePublisher dataPub) {
      this.publisher = dataPub;

      this.serialId = new SerialId();
      this.serialId.setBundleId(bundleId.incrementAndGet());
      this.bsmDecoder = new BsmDecoderHelper();
      this.timDecoder = new TimDecoderHelper();
   }
}
