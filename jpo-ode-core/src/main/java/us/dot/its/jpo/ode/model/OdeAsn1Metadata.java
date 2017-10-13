package us.dot.its.jpo.ode.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public class OdeAsn1Metadata extends OdeLogMetadata {

   private static final long serialVersionUID = -8601265839394150140L;

   private List<Asn1Encoding> encodings = new ArrayList<Asn1Encoding>();

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

   public OdeAsn1Metadata(JsonNode metadata) {
      setEncodings(metadata.get("encodings").get("encodings"));
      setGeneratedAt(metadata.get("generatedAt").asText());
      setLogFileName(metadata.get("logFileName").asText());
      setPayloadType(metadata.get("payloadType").asText());
      setReceivedAt(metadata.get("receivedAt").asText());
      setSanitized(metadata.get("sanitized").asBoolean());
      setSchemaVersion(metadata.get("schemaVersion").asInt());
      setSerialId(new SerialId(metadata.get("serialId")));
      setValidSignature(metadata.get("validSignature").asBoolean());
   }

   private void setEncodings(JsonNode encodings) {
      if (encodings.isArray()) {
         Iterator<JsonNode> elements = encodings.elements();

         while (elements.hasNext()) {
            this.encodings.add(new Asn1Encoding(elements.next()));
         }
      }
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
