package us.dot.its.jpo.ode.plugin.asn1;

import java.io.InputStream;

import us.dot.its.jpo.ode.plugin.OdePlugin;

public interface Asn1Plugin extends OdePlugin {

    Asn1Object decodeUPERBase64(String base64Msg);

    Asn1Object decodeUPERMessageFrameHex(String hexMsg);
    Asn1Object decodeUPERBsmHex(String hexMsg);

    String UPER_EncodeBase64(Asn1Object asn1Object);

    String UPER_EncodeHex(Asn1Object asn1Object);

    byte[] UPER_EncodeBytes(Asn1Object asn1Object);

    Asn1Object decodeUPERBsmBytes(byte[] byteArrayMsg);

    Asn1Object decodeUPERMessageFrameBytes(byte[] byteArrayMsg);

    Asn1Object decodeUPERMessageFrameStream(InputStream ins);

    Asn1Object decodeUPERBsmStream(InputStream ins);

}
