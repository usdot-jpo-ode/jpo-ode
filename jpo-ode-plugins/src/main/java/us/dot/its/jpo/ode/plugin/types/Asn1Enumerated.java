package us.dot.its.jpo.ode.plugin.types;

/**
 * Class for an ASN.1 enumerated object.
 */
public interface Asn1Enumerated extends Asn1Type {
  int getIndex();

  String getName();

  boolean hasExtensionMarker();

  int maxIndex();

}
