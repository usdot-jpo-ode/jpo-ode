package us.dot.its.jpo.ode.model;

import java.util.HashSet;
import java.util.Set;

public class OdeAsn1Metadata extends OdeLogMetadata {

    private static final long serialVersionUID = -8601265839394150140L;
    
    private Set<Asn1Encoding> encodings = new HashSet<Asn1Encoding>();
    
    public OdeAsn1Metadata() {
        super();
    }

    public OdeAsn1Metadata(String payloadType, SerialId serialId, String receivedAt) {
        super(payloadType, serialId, receivedAt);
    }

    public OdeAsn1Metadata(OdeMsgPayload payload, SerialId serialId, String receivedAt, String generatedAt) {
        super(payload, serialId, receivedAt, generatedAt);
    }

    public OdeAsn1Metadata(OdeMsgPayload payload) {
        super(payload);
    }

    public OdeAsn1Metadata(OdeMsgPayload payload, String generatedAt) {
        super(payload);
        this.generatedAt = generatedAt;
    }

    public OdeAsn1Metadata(String payloadType, SerialId serialId, String receivedAt, String generatedAt) {
        super(payloadType, serialId, receivedAt);
        this.generatedAt = generatedAt;
    }

   public Set<Asn1Encoding> getEncodings() {
      return encodings;
   }

   public void setEncodings(Set<Asn1Encoding> encodings) {
      this.encodings = encodings;
   }

   public OdeAsn1Metadata addEncoding(Asn1Encoding encoding) {
      encodings.add(encoding);
      return this;
   }
}
