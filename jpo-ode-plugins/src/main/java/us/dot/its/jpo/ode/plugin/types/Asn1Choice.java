package us.dot.its.jpo.ode.plugin.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import us.dot.its.jpo.ode.plugin.serialization.SerializationUtil;

/**
 * Base class for an ASN.1 choice.
 */
public abstract class Asn1Choice implements Asn1Type {

  @JsonIgnore
  final boolean hasExtensionMarker;

  public Asn1Choice(boolean hasExtensionMarker) {
    this.hasExtensionMarker = hasExtensionMarker;
  }

  @Override
  public String toString() {
    ObjectMapper mapper = SerializationUtil.jsonMapper();
    try {
      return mapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      System.err.println(e.getMessage());
      return "";
    }
  }
}
