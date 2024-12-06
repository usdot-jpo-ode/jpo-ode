/*******************************************************************************
 * Copyright 2018 572682
 * 
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   <p>http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package us.dot.its.jpo.ode.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper.Builder;
import org.json.JSONObject;
import org.json.XML;

/**
 * Utility class for XML manipulation.
 */
public class XmlUtils {
  /**
   * Custom XML exception for handling XML parsing errors.
   */
  public static class XmlUtilsException extends Exception {

    private static final long serialVersionUID = 1L;

    public XmlUtilsException(String string) {
      super(string);
    }

    public XmlUtilsException(String string, Exception e) {
      super(string, e);
    }

  }

  private XmlMapper xmlMapper = new XmlMapper();
  private static XmlMapper staticXmlMapper = new XmlMapper();

  static {
    var builder = new Builder(staticXmlMapper);
    builder.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    builder.defaultUseWrapper(true);
    staticXmlMapper = builder.build();
  }

  /**
   * Instantiates the XML utility as an object instead of using static methods.
   */
  public XmlUtils() {
    super();
    var builder = new Builder(xmlMapper);
    builder.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    builder.defaultUseWrapper(true);
    xmlMapper = builder.build();
  }

  public String toXml(Object o) throws JsonProcessingException {
    String xml = xmlMapper.writeValueAsString(o);
    return xml;
  }

  // public static String toXml(Object o) throws XmlUtilsException {
  // try {
  // JSONObject root = new JSONObject();
  // JSONObject object = new JSONObject(o);
  // root.put(o.getClass().getSimpleName(), object);
  // return XML.toString(root);
  // } catch (JSONException e) {
  // throw new XmlUtilsException("Error encoding object to XML", e);
  // }
  // }

  /**
   * Attempt to convert an XML String into the specified class type.
   *
   * @param xml The XML String value
   * @param clazz The class type
   * @return The deserialized object that is of type clazz
   */
  public Object fromXml(String xml, Class<?> clazz) throws XmlUtilsException {
    try {
      return xmlMapper.readValue(xml, clazz);
    } catch (Exception e) {
      throw new XmlUtilsException("Error decoding " + xml + " to " + clazz.getName(), e);
    }
  }

  /**
   * Embeds the arrayNode into an ObjectNode with the given childKey. By default a
   * JSON array such as {"parent":[1, 2, 3,]} will be converted to:
   * <ObjectNode><parent>1</parent><parent>2</parent><parent>3</parent></ObjectNode>.
   * This is not often desired as there is no paren object to encompass the array.
   * By calling this method given childKey = "child" and arrayNode = [1, 2, 3,],
   * method will return {"parent":{"child":[1, 2, 3,]}} which as a result will be
   * encoded to
   * <ObjectNode><parent><child>1</child><child>2</child><child>3</child></parent></ObjectNode>.
   * Which is a more representative of the JSON ObjectNode.
   *
   * @param childKey The key to be given to the child array object
   * @param arrayNode The array node to be embedded in a ObjectNode
   * @return OBjectNode representation of the given arrayNode redy to be converted
   *         to XML
   */
  public static ObjectNode createEmbeddedJsonArrayForXmlConversion(String childKey, 
      JsonNode arrayNode) {
    ObjectNode childNode = staticXmlMapper.createObjectNode();
    childNode.set(childKey, arrayNode);
    return childNode;
  }

  /**
   * Find a component of an XML string by specifying the tag name.
   *
   * @param xml The XML String to be searched
   * @param tagName The tag name to be identified
   * @return The XML String only consisting of the tag and its children
   */
  public static String findXmlContentString(String xml, String tagName) {
    // Construct the start and end tag strings
    String startTag = "<" + tagName + ">";
    String endTag = "</" + tagName + ">";

    // Find the start index of the start tag
    int startIndex = xml.indexOf(startTag);
    if (startIndex == -1) {
      // Tag not found
      return null;
    }

    // Find the end index of the end tag, after the start tag
    int endIndex = xml.indexOf(endTag, startIndex);
    if (endIndex == -1) {
      // End tag not found
      return null;
    }

    // Add the length of the end tag to get the complete end index
    endIndex += endTag.length();

    return xml.substring(startIndex, endIndex);
  }

  /**
   * Static method to attempt to serialize an object into XML.
   *
   * @param o The object to be serialized
   * @return The serialized XML String
   * @throws XmlUtilsException Throws an exception when failing to serialize the object
   */
  public static String toXmlStatic(Object o) throws XmlUtilsException {
    String xml;
    try {
      xml = staticXmlMapper.writeValueAsString(o);
    } catch (Exception e) {
      throw new XmlUtilsException("Error encoding object to XML", e);
    }
    return xml;
  }

  /**
   * Static method to attempt to deserialize an XML String into a specified object type.
   *
   * @param xml The xml String to be deserialized
   * @param clazz The class type
   * @return The deserialized object of class type clazz
   * @throws XmlUtilsException Throws an exception when failing to deserialize the XML String
   */
  public static Object fromXmlS(String xml, Class<?> clazz) throws XmlUtilsException {
    try {
      return staticXmlMapper.readValue(xml, clazz);
    } catch (Exception e) {
      throw new XmlUtilsException("Error decoding " + xml + " to " + clazz.getName(), e);
    }
  }

  /**
   * Static method to attempt to transform an XML String into an ObjectNode.
   *
   * @param xml The xml String to be transformed
   * @return An ObjectNode representing the XML
   * @throws XmlUtilsException Throws an exception when failing to transform into an ObjectNode
   */
  public static ObjectNode toObjectNode(String xml) throws XmlUtilsException {
    try {
      JSONObject jsonObject = XML.toJSONObject(xml, true);
      String jsonString = jsonObject.toString();
      return JsonUtils.toObjectNode(jsonString);

      /*
       * Due to issues with XmlMapper converting "xml arrays" to a valid DOM
       * collection we could not use it in this context. Hence the above workaround
       * was adopted. See:
       * https://github.com/FasterXML/jackson-dataformat-xml/issues/187
       * https://github.com/FasterXML/jackson-dataformat-xml/issues/205
       */
      // return (ObjectNode) staticXmlMapper.readTree(xml);
    } catch (Exception e) {
      throw new XmlUtilsException("Error decoding " + xml + "to ObjectNode", e);
    }
  }

  /**
   * Static method to attempt to transform an XML String into a JSONObject.
   *
   * @param xml The xml String to be transformed
   * @return A JSONObject representing the XML
   * @throws XmlUtilsException Throws an exception when failing to transform into an JSONObject
   */
  public static JSONObject toJSONObject(String xml) throws XmlUtilsException {
    try {
      return XML.toJSONObject(xml, true);
    } catch (Exception e) {
      throw new XmlUtilsException("Error decoding " + xml + "to JSONObject", e);
    }
  }

  /**
   * Get a specific JSON node from an XML String based on a field name.
   *
   * @param tree The xml String to be parsed
   * @param fieldName The field name to be parsed for
   * @return The JsonNode for the specified field name
   * @throws XmlUtilsException Throws an exception when failing to parse the XML String
   */
  public static JsonNode getJsonNode(String tree, String fieldName) throws XmlUtilsException {
    JsonNode jsonNode;
    try {
      jsonNode = staticXmlMapper.readTree(tree);
    } catch (Exception e) {
      throw new XmlUtilsException("Error getting field name " + fieldName + " from " + tree, e);
    }
    return jsonNode.get(fieldName);
  }

  public XmlMapper getXmlMapper() {
    return xmlMapper;
  }

  public static XmlMapper getStaticXmlMapper() {
    return staticXmlMapper;
  }

}
