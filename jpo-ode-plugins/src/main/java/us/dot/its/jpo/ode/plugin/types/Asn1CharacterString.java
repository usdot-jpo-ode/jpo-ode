package us.dot.its.jpo.ode.plugin.types;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Base class for an ASN.1 character string.
 */
public abstract class Asn1CharacterString implements Asn1Type {

  protected final int minLength;
  protected final int maxLength;
  protected String value;

  public Asn1CharacterString(int minLength, int maxLength) {
    this.minLength = minLength;
    this.maxLength = maxLength;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  /**
   * Sets the character string to value.
   *
   * @param value The String value that the Asn1CharacterString will be set to.
   */
  public void setValue(String value) {
    if (!validate(value)) {
      throw new IllegalArgumentException(
          String.format("String '%s' has invalid length. Must be between %d and %s",
              value, minLength, maxLength));
    }
    this.value = value;
  }

  protected boolean validate(String valueA) {
    if (valueA == null) {
      return true;
    }
    return valueA.length() >= minLength && valueA.length() <= maxLength;
  }

  @Override
  public String toString() {
    return value;
  }

}
