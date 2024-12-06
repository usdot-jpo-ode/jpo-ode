package us.dot.its.jpo.ode.plugin.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * Utility class to perform serialization and deserialization with static JSON and XML mappers.
 *
 * @author Ivan Yourshaw
 */
public class SerializationUtil {

  private static final ObjectMapper jsonMapper = new ObjectMapper();
  private static final XmlMapper xmlMapper = new XmlMapper();

  public static ObjectMapper jsonMapper() {
    return jsonMapper;
  }

  public static XmlMapper xmlMapper() {
    return xmlMapper;
  }
}
