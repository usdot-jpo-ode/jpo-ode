package us.dot.its.jpo.ode.plugin.types;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Class for an ASN.1 octet string.
 */
public class Asn1OctetString implements Asn1Type {

  protected final int minLength;
  protected final int maxLength;
  protected String value;

  @JsonValue
  public String getValue() {
    return value;
  }

  public Asn1OctetString(int minLength, int maxLength) {
    this.minLength = minLength;
    this.maxLength = maxLength;
  }

  public boolean validate(String aValue) {
    if (aValue == null) return true;
    // Size of hex format string can be 2 * byte size
    // TODO validate valid hex string digit are
    return aValue.length() >= 2 * minLength && aValue.length() <= 2 * maxLength;
  }

}
