package us.dot.its.jpo.ode.plugin.asn1;

import java.io.InputStream;

import us.dot.its.jpo.ode.plugin.OdePlugin;

public interface Asn1Plugin extends OdePlugin {

    Asn1Object UPER_DecodeBase64(String base64Msg);

    Asn1Object UPER_DecodeMessageFrameHex(String hexMsg);
    Asn1Object UPER_DecodeBsmHex(String hexMsg);


    String UPER_DecodeBase64ToJson(String base64Msg);

    String UPER_DecodeHexToJson(String hexMsg);

    String UPER_DecodeBytesToJson(byte[] byteArrayMsg);

    String UPER_EncodeBase64(Asn1Object asn1Object);

    String UPER_EncodeHex(Asn1Object asn1Object);

    byte[] UPER_EncodeBytes(Asn1Object asn1Object);

    String UPER_EncodeBase64FromJson(String asn1Object);

    String UPER_EncodeHexfromJson(String asn1Object);

    byte[] UPER_EncodeBytesFromJson(String asn1Object);

    Asn1Object UPER_DecodeBsmBytes(byte[] byteArrayMsg);

    Asn1Object UPER_DecodeMessageFrameBytes(byte[] byteArrayMsg);

    Asn1Object UPER_DecodeMessageFrameStream(InputStream ins);

    Asn1Object UPER_DecodeBsmStream(InputStream ins);

}
