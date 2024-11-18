/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.LogicalType;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

@Slf4j
public class JsonUtils {

   public static class JsonUtilsException extends Exception {

      private static final long serialVersionUID = 1L;

      public JsonUtilsException(String string, Exception e) {
         super(string, e);
      }

   }

   private static final ObjectMapper mapper;
   private static final ObjectMapper mapper_noNulls;

   private JsonUtils() {
   }

   static {
      mapper = new ObjectMapper();
      mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
      mapper.coercionConfigFor(LogicalType.Enum)
            .setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsNull);

      mapper_noNulls = new ObjectMapper();
      mapper_noNulls.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
      mapper_noNulls.setSerializationInclusion(Include.NON_NULL);
   }

   public static String toJson(Object o, boolean verbose) {
      // convert java object to JSON format,
      // and returned as JSON formatted string
      try {
         return verbose ? mapper.writeValueAsString(o) : mapper_noNulls.writeValueAsString(o);
      } catch (JsonProcessingException e) {
         log.error("Error converting object to JSON", e);
         return "";
      }
   }

   public static Object fromJson(String s, Class<?> clazz) {
      try {
         return jacksonFromJson(s, clazz);
      } catch (JsonUtilsException e) {
         log.error("Error deserializing JSON tree to {}", clazz.getName(), e);
         return null;
      }
   }

   public static Object jacksonFromJson(String s, Class<?> clazz) throws JsonUtilsException {
      try {
         return mapper.readValue(s, clazz);
      } catch (IOException e) {
         throw new JsonUtilsException("Error deserializing JSON tree to " + clazz.getName(), e);
      }
   }

   public static Object jacksonFromJson(String s, Class<?> clazz, boolean allowUnknownProperties)
         throws JsonUtilsException {
      try {
         mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, !allowUnknownProperties);
         return mapper.readValue(s, clazz);
      } catch (IOException e) {
         throw new JsonUtilsException("Error deserializing JSON tree to " + clazz.getName(), e);
      } finally {
         mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, allowUnknownProperties);
      }
   }

   public static String newJson(String key, Object value) {
      return newObjectNode(key, value).toString();
   }

   public static ObjectNode newObjectNode(String key, Object value) {
      ObjectNode json = mapper.createObjectNode();
      json.putPOJO(key, value);
      return json;
   }

   public static ObjectNode addNode(ObjectNode tree, String fieldName, Object fieldValue) {
      tree.putPOJO(fieldName, fieldValue);
      return tree;
   }

   public static JsonNode getJsonNode(String tree, String fieldName) {
      JsonNode node = null;
      try {
         JsonNode jsonNode = mapper.readTree(tree);
         node = jsonNode.get(fieldName);

      } catch (IOException e) {
         log.error("IOException", e);
      }
      return node;
   }

   public static ObjectNode newNode() {
      return mapper.createObjectNode();
   }

   public static ArrayNode newArrayNode() {
      return mapper.createArrayNode();
   }

   public static ObjectNode toObjectNode(String tree) throws JsonUtilsException {
      ObjectNode jsonNode;
      try {
         jsonNode = (ObjectNode) mapper.readTree(tree);
      } catch (Exception e) {
         throw new JsonUtilsException("Error converting JSON tree to ObjectNode", e);
      }
      return jsonNode;
   }

   public static JSONObject toJSONObject(String json) throws JsonUtilsException {
      try {
         return new JSONObject(json);
      } catch (Exception e) {
         throw new JsonUtilsException("Error decoding " + json + "to JSONObject", e);
      }
   }

   public static boolean isValid(String tree) throws IOException {
      try {
         ObjectNode jsonNode = (ObjectNode) mapper.readTree(tree);
         return jsonNode != null;
      } catch (JsonProcessingException jpe) {
         return false;
      }
   }

   public static HashMap<String, JsonNode> jsonNodeToHashMap(JsonNode jsonNode) {
      HashMap<String, JsonNode> nodeProps = new HashMap<String, JsonNode>();
      Iterator<Entry<String, JsonNode>> iter = jsonNode.fields();

      while (iter.hasNext()) {
         Entry<String, JsonNode> element = iter.next();
         nodeProps.put(element.getKey(), element.getValue());
      }
      return nodeProps;
   }

   /**
    * Takes in a key, value pair and returns a valid JSON string such as
    * {"error":"message"}
    * 
    * @param key
    * @param value
    * @return
    */
   public static String jsonKeyValue(String key, String value) {
      return "{\"" + key + "\":\"" + value + "\"}";
   }

   public static BigDecimal decimalValue(JsonNode v) {
      BigDecimal result;
      if (v.isTextual()) {
         result = new BigDecimal(v.textValue());
      } else {
         result = v.decimalValue();
      }
      return result;
   }
}
