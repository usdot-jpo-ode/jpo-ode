package us.dot.its.jpo.ode.plugin.types;

/**
 * Class for an ASN.1 octet string.
 */
public class Asn1OctetString extends Asn1CharacterString {

  public Asn1OctetString(int minLength, int maxLength) {
    super(minLength, maxLength);
  }

}
