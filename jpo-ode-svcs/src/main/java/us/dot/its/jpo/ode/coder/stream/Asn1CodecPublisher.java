package us.dot.its.jpo.ode.coder.stream;

public interface Asn1CodecPublisher {
   
   void publish(byte[] asn1Encoding) throws Exception;
}
