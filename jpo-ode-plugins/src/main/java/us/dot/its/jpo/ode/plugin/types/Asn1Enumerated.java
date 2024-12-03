package us.dot.its.jpo.ode.plugin.types;

public interface Asn1Enumerated extends Asn1Type {
    int getIndex();
    String getName();
    boolean hasExtensionMarker();
    int maxIndex();


}
