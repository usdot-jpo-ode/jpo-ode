package us.dot.its.jpo.ode.model;

import java.io.IOException;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.util.JsonUtils;

public class InternalDataMessage extends OdeObject {
   private static final long serialVersionUID = 9152243091714512036L;
   
   private String key;
   private OdeMsgPayload payload;
   private OdeMetadata metadata;
   
   public InternalDataMessage() {
      super();
   }
   public InternalDataMessage(String key, OdeMsgPayload payload, OdeMetadata metadata) {
      super();
      this.key = key;
      setMetadata(metadata);
      setPayload(payload);
   }
   public InternalDataMessage(String key, OdeMsgPayload payload) {
      this(key, payload, new OdeMetadata()
            .setKey(key)
            .setPayloadType(
                  payload.getDataType() != null ?
                        payload.getDataType().getShortName() :
                           OdeDataType.Unknown.getShortName()));
   }
   public OdeMsgPayload getPayload() {
      return payload;
   }
   public InternalDataMessage setPayload(OdeMsgPayload payload) {
      this.payload = payload;
      if (getMetadata() != null)
         getMetadata().setPayloadType(payload.getDataType() != null ?
               payload.getDataType().getShortName() :
                  OdeDataType.Unknown.getShortName());
      return this;
   }
   public String getKey() {
      return key;
   }
   public InternalDataMessage setKey(String key) {
      this.key = key;
      return this;
   }
   public OdeMetadata getMetadata() {
      return metadata;
   }
   public InternalDataMessage setMetadata(OdeMetadata metadata) {
      this.metadata = metadata;
      return this;
   }
   
   public static ObjectNode createObjectNodeFromPayload(JsonNode payload, JsonNode metadata) 
         throws JsonProcessingException, IOException {
      ObjectNode idm = null;
      if (payload != null && metadata != null) {
         idm = buildJsonObjectNode(payload, metadata);
      }
      return idm;
   }
   public static ObjectNode jsonStringToObjectNode(String data)
         throws JsonProcessingException, IOException {
      ObjectNode idm = null;
      ObjectNode jsonObject = JsonUtils.toObjectNode(data);
      JsonNode payload = jsonObject.get(AppContext.PAYLOAD_STRING);
      JsonNode metadata = jsonObject.get(AppContext.METADATA_STRING);
      idm = createObjectNodeFromPayload(payload, metadata);
      return idm;
   }
   
   private static ObjectNode buildJsonObjectNode(JsonNode payload, JsonNode metadata) {
      ObjectNode idm;
      idm = JsonUtils.newNode();
      
      HashMap<String, JsonNode> metadataProps = JsonUtils.jsonNodeToHashMap(metadata);
      idm.putObject(AppContext.METADATA_STRING).setAll(metadataProps);
      
      HashMap<String, JsonNode> payloadProps = JsonUtils.jsonNodeToHashMap(payload);
      idm.putObject(AppContext.PAYLOAD_STRING).setAll(payloadProps);
      
      return idm;
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((key == null) ? 0 : key.hashCode());
      result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
      result = prime * result + ((payload == null) ? 0 : payload.hashCode());
      return result;
   }
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      InternalDataMessage other = (InternalDataMessage) obj;
      if (key == null) {
         if (other.key != null)
            return false;
      } else if (!key.equals(other.key))
         return false;
      if (metadata == null) {
         if (other.metadata != null)
            return false;
      } else if (!metadata.equals(other.metadata))
         return false;
      if (payload == null) {
         if (other.payload != null)
            return false;
      } else if (!payload.equals(other.payload))
         return false;
      return true;
   }
}