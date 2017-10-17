package us.dot.its.jpo.ode.coder.stream;

import java.io.BufferedInputStream;

public interface Asn1CodecPublisher {
   
   public void publish(BufferedInputStream is, String fileName) throws Exception;
}
