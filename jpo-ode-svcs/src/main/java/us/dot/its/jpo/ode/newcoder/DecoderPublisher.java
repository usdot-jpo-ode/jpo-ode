package us.dot.its.jpo.ode.newcoder;

import java.io.InputStream;

public interface DecoderPublisher {
   
   public void decodeAndPublish(InputStream is, String fileName) throws Exception;
}
