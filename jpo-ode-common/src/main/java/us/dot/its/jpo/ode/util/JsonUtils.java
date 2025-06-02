/*******************************************************************************
 * Copyright 2018 572682
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *   http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package us.dot.its.jpo.ode.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFormat;
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
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

/**
 * Utility class for JSON operations using Jackson ObjectMapper.
 */
@Slf4j
public class JsonUtils {

  private static final ObjectMapper mapper;
  private static final ObjectMapper mapper_noNulls;

  static {
    mapper = new ObjectMapper();
    mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
    mapper.coercionConfigFor(LogicalType.Enum)
        .setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsNull);

    mapper_noNulls = new ObjectMapper();
    mapper_noNulls.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
    mapper_noNulls.setSerializationInclusion(Include.NON_NULL);

    // Ensure BigDecimals are serialized consistently as numbers not strings
    mapper.configOverride(BigDecimal.class).setFormat(JsonFormat.Value.forShape(JsonFormat.Shape.NUMBER));
    mapper_noNulls.configOverride(BigDecimal.class).setFormat(JsonFormat.Value.forShape(JsonFormat.Shape.NUMBER));
  }

  private JsonUtils() {
    // Private constructor to prevent instantiation
  }

  /**
   * Converts an object to JSON string.
   *
   * @param o       the object to convert
   * @param verbose if true, includes null values in output
   * @return JSON string representation of the object
   */
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

  /**
   * Converts a JSON string to an object of the specified class.
   *
   * @param s     the JSON string
   * @param clazz the target class
   * @return the deserialized object, or null if deserialization fails
   */
  public static Object fromJson(String s, Class<?> clazz) {
    try {
      return jacksonFromJson(s, clazz);
    } catch (JsonUtilsException e) {
      log.error("Error deserializing JSON tree to {}", clazz.getName(), e);
      return null;
    }
  }

  /**
   * Converts a JSON string to an object using Jackson's ObjectMapper.
   *
   * @param s     the JSON string
   * @param clazz the target class
   * @return the deserialized object
   * @throws JsonUtilsException if deserialization fails
   */
  public static Object jacksonFromJson(String s, Class<?> clazz) throws JsonUtilsException {
    try {
      return mapper.readValue(s, clazz);
    } catch (IOException e) {
      throw new JsonUtilsException("Error deserializing JSON tree to " + clazz.getName(), e);
    }
  }

  /**
   * Converts a JSON string to an object with control over unknown properties.
   *
   * @param s                      the JSON string
   * @param clazz                  the target class
   * @param allowUnknownProperties whether to allow unknown properties
   * @return the deserialized object
   * @throws JsonUtilsException if deserialization fails
   */
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

  /**
   * Creates a new JSON string with a single key-value pair.
   *
   * @param key   the key
   * @param value the value
   * @return JSON string containing the key-value pair
   */
  public static String newJson(String key, Object value) {
    return newObjectNode(key, value).toString();
  }

  /**
   * Creates a new ObjectNode with a single key-value pair.
   *
   * @param key   the key
   * @param value the value
   * @return ObjectNode containing the key-value pair
   */
  public static ObjectNode newObjectNode(String key, Object value) {
    ObjectNode json = mapper.createObjectNode();
    json.putPOJO(key, value);
    return json;
  }

  /**
   * Adds a new field to an existing ObjectNode.
   *
   * @param tree       the existing ObjectNode
   * @param fieldName  the name of the new field
   * @param fieldValue the value of the new field
   * @return the modified ObjectNode
   */
  public static ObjectNode addNode(ObjectNode tree, String fieldName, Object fieldValue) {
    tree.putPOJO(fieldName, fieldValue);
    return tree;
  }

  /**
   * Retrieves a JsonNode from a JSON string by field name.
   *
   * @param tree      the JSON string
   * @param fieldName the name of the field to retrieve
   * @return the JsonNode for the specified field, or null if not found
   */
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

  /**
   * Creates a new empty ObjectNode.
   *
   * @return new empty ObjectNode
   */
  public static ObjectNode newNode() {
    return mapper.createObjectNode();
  }

  /**
   * Creates a new empty ArrayNode.
   *
   * @return new empty ArrayNode
   */
  public static ArrayNode newArrayNode() {
    return mapper.createArrayNode();
  }

  /**
   * Converts a JSON string to an ObjectNode.
   *
   * @param tree the JSON string
   * @return the converted ObjectNode
   * @throws JsonUtilsException if conversion fails
   */
  public static ObjectNode toObjectNode(String tree) throws JsonUtilsException {
    ObjectNode jsonNode;
    try {
      jsonNode = (ObjectNode) mapper.readTree(tree);
    } catch (Exception e) {
      throw new JsonUtilsException("Error converting JSON tree to ObjectNode", e);
    }
    return jsonNode;
  }

  /**
   * Converts a JSON string to a JSONObject.
   *
   * @param json the JSON string
   * @return the converted JSONObject
   * @throws JsonUtilsException if conversion fails
   */
  public static JSONObject toJSONObject(String json) throws JsonUtilsException {
    try {
      return new JSONObject(json);
    } catch (Exception e) {
      throw new JsonUtilsException("Error decoding " + json + "to JSONObject", e);
    }
  }

  /**
   * Validates if a string is valid JSON.
   *
   * @param tree the string to validate
   * @return true if valid JSON, false otherwise
   * @throws IOException if an I/O error occurs
   */
  public static boolean isValid(String tree) throws IOException {
    try {
      ObjectNode jsonNode = (ObjectNode) mapper.readTree(tree);
      return jsonNode != null;
    } catch (JsonProcessingException jpe) {
      return false;
    }
  }

  /**
   * Converts a JsonNode to a HashMap.
   *
   * @param jsonNode the JsonNode to convert
   * @return HashMap containing the JsonNode's fields
   */
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
   * Creates a JSON string with a single key-value pair.
   *
   * @param key   the key
   * @param value the value
   * @return formatted JSON string
   */
  public static String jsonKeyValue(String key, String value) {
    return "{\"" + key + "\":\"" + value + "\"}";
  }

  /**
   * Converts a JsonNode to a BigDecimal, handling both numeric and text
   * representations.
   *
   * @param v the JsonNode to convert
   * @return the BigDecimal value
   */
  public static BigDecimal decimalValue(JsonNode v) {
    BigDecimal result;
    if (v.isTextual()) {
      result = new BigDecimal(v.textValue());
    } else {
      result = v.decimalValue();
    }
    return result;
  }

  /**
   * Exception class for JSON utility operations.
   */
  public static class JsonUtilsException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new JsonUtilsException with the specified detail message and
     * cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public JsonUtilsException(String message, Exception cause) {
      super(message, cause);
    }
  }
}
