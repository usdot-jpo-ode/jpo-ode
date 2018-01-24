package us.dot.its.jpo.ode.model;

import java.util.ArrayList;
import java.util.List;

public class OdeAsn1Metadata extends OdeLogMetadata {

   private static final long serialVersionUID = -8601265839394150140L;

   private List<Asn1Encoding> encodings = new ArrayList<Asn1Encoding>();

   private ReceivedMessageDetails receivedMessageDetails;
   
   public ReceivedMessageDetails getReceivedMessageDetails() {
      return receivedMessageDetails;
   }

   public void setReceivedMessageDetails(ReceivedMessageDetails receivedMessageDetails) {
      this.receivedMessageDetails = receivedMessageDetails;
   }

   public OdeAsn1Metadata() {
      super();
   }

   public OdeAsn1Metadata(OdeMsgPayload payload) {
      super(payload);
   }

   public List<Asn1Encoding> getEncodings() {
      return encodings;
   }

   public void setEncodings(List<Asn1Encoding> encodings) {
      this.encodings = encodings;
   }

   public OdeAsn1Metadata addEncoding(Asn1Encoding encoding) {
      encodings.add(encoding);
      return this;
   }
}
