package us.dot.its.jpo.ode.coder.stream;

import java.io.BufferedInputStream;
import java.util.concurrent.atomic.AtomicLong;

import us.dot.its.jpo.ode.coder.BsmDecoderHelper;
import us.dot.its.jpo.ode.coder.OdeDataPublisher;
import us.dot.its.jpo.ode.coder.TimDecoderHelper;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterFileType;
import us.dot.its.jpo.ode.model.SerialId;

public abstract class AbstractDecoderPublisher implements DecoderPublisher {

   protected SerialId serialId;

   protected OdeDataPublisher bsmMessagePublisher;

   protected BsmDecoderHelper bsmDecoder;
   protected TimDecoderHelper timDecoder;

   protected static AtomicLong bundleId = new AtomicLong(0);

   public AbstractDecoderPublisher(OdeDataPublisher bsmMessagePublisher) {
      this.bsmMessagePublisher = bsmMessagePublisher;

      this.serialId = new SerialId();
      this.bsmDecoder = new BsmDecoderHelper();
      this.timDecoder = new TimDecoderHelper();
   }

   public abstract void decodeAndPublish(BufferedInputStream is, String fileName, ImporterFileType fileType) throws Exception;
}
