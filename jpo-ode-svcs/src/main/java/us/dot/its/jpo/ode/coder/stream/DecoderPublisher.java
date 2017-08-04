package us.dot.its.jpo.ode.coder.stream;

import java.io.InputStream;

public interface DecoderPublisher {
   
   public void decodeAndPublish(InputStream is, String fileName) throws Exception;
}
