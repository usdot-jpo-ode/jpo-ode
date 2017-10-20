package us.dot.its.jpo.ode.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OdeAsn1Metadata extends OdeLogMetadata {

   private static final long serialVersionUID = -8601265839394150140L;

   private List<Asn1Encoding> encodings = new ArrayList<Asn1Encoding>();

   public OdeAsn1Metadata() {
      super();
   }

   public OdeAsn1Metadata(OdeMsgPayload payload) {
      super(payload);
   }

   public OdeAsn1Metadata(JsonNode metadata) {
      setEncodings(metadata.get("encodings").get("encodings"));
      setRecordGeneratedAt(metadata.get("generatedAt").asText());
      setRecordGeneratedBy(GeneratedBy.valueOf(metadata.get("generatedBy").asText()));
      setLogFileName(metadata.get("logFileName").asText());
      setRecordType(metadata.get("recordType").asText());
      setPayloadType(metadata.get("payloadType").asText());
      setOdeReceivedAt(metadata.get("odReceivedAt").asText());
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
