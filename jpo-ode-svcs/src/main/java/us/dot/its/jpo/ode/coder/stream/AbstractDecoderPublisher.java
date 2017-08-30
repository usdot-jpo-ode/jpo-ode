package us.dot.its.jpo.ode.coder.stream;

import java.io.BufferedInputStream;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.coder.BsmDecoderHelper;
import us.dot.its.jpo.ode.coder.MessagePublisher;
import us.dot.its.jpo.ode.importer.BsmFileParser;
import us.dot.its.jpo.ode.model.SerialId;

public abstract class AbstractDecoderPublisher implements DecoderPublisher {

   protected static final Logger logger = LoggerFactory.getLogger(AbstractDecoderPublisher.class);

   protected SerialId serialId;

   protected MessagePublisher publisher;

   protected BsmDecoderHelper bsmDecoder;
   protected BsmFileParser bsmFileParser;

   protected static AtomicInteger bundleId = new AtomicInteger(1);

   public AbstractDecoderPublisher(
       MessagePublisher dataPub) {
      this.publisher = dataPub;

      this.serialId = new SerialId();
      this.serialId.setBundleId(bundleId.incrementAndGet());
      this.bsmDecoder = new BsmDecoderHelper();
      this.bsmFileParser = new BsmFileParser();
   }

   @Override
   public abstract void decodeAndPublish(BufferedInputStream is, String fileName) throws Exception;}
