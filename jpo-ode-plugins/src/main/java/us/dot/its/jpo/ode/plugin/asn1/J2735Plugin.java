package us.dot.its.jpo.ode.plugin.asn1;

import java.io.InputStream;

import us.dot.its.jpo.ode.plugin.OdePlugin;

public interface J2735Plugin extends OdePlugin {

    Asn1Object decodeUPERMessageFrameHex(String hexMsg);
    Asn1Object decodeUPERBsmHex(String hexMsg);

    String encodeUPERBase64(Asn1Object asn1Object);

    String encodeUPERHex(Asn1Object asn1Object);

    byte[] encodeUPERBytes(Asn1Object asn1Object);

    Asn1Object decodeUPERBsmBytes(byte[] byteArrayMsg);

    Asn1Object decodeUPERMessageFrameBytes(byte[] byteArrayMsg);

    Asn1Object decodeUPERMessageFrameStream(InputStream ins);

    Asn1Object decodeUPERBsmStream(InputStream ins);

}
