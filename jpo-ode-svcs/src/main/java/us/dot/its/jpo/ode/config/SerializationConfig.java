package us.dot.its.jpo.ode.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper.Builder;
import java.math.BigDecimal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration class for customizing serialization settings using JSON and XML serialization.
 */
@Configuration
public class SerializationConfig {

  /**
   * Configures and returns an {@link ObjectMapper} instance customized for specific serialization
   * and deserialization behavior.
   *
   * @return a customized {@link ObjectMapper} instance supporting specific serialization and
   *         deserialization configurations.
   */
  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
    mapper.coercionConfigFor(LogicalType.Enum)
        .setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsNull);
    // Ensure BigDecimals are serialized consistently as numbers not strings
    mapper.configOverride(BigDecimal.class).setFormat(JsonFormat.Value.forShape(JsonFormat.Shape.NUMBER));
    // Only serialize non-null fields
    mapper.setSerializationInclusion(Include.NON_NULL);
    return mapper;
  }

  /**
   * Configures and returns an {@link XmlMapper} instance with customized serialization
   * and deserialization behavior for XML processing.
   *
   * @return a customized {@link XmlMapper} instance with specific configurations,
   *         including disabled failure on unknown properties and default use of wrappers.
   */
  @Bean
  public XmlMapper xmlMapper() {
    XmlMapper xmlMapper = new XmlMapper();
    var builder = new Builder(xmlMapper);
    builder.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    builder.defaultUseWrapper(true);
    return builder.build();
  }


  @Bean("simpleObjectMapper")
  public ObjectMapper simpleObjectMapper() {
    return new ObjectMapper();
  }

  @Bean("simpleXmlMapper")
  public XmlMapper simpleXmlMapper() {
    return new XmlMapper();
  }
}
