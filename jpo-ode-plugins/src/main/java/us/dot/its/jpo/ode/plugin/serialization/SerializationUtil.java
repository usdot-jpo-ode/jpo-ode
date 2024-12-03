package us.dot.its.jpo.ode.plugin.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

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
