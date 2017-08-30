package us.dot.its.jpo.ode.coder.stream;

import java.io.BufferedInputStream;

public interface DecoderPublisher {
   
   public void decodeAndPublish(BufferedInputStream is, String fileName, boolean hasMetadataHeader) throws Exception;
}
