package us.dot.its.jpo.ode.plugin.asn1;

import us.dot.its.jpo.ode.plugin.OdePluginImpl;

public abstract class Asn1Plugin extends OdePluginImpl {
	
	public abstract Asn1Object UPER_DecodeBase64 (String base64Msg);
	public abstract Asn1Object UPER_DecodeHex (String hexMsg);
	public abstract Asn1Object UPER_DecodeBytes (byte[] byteArrayMsg);
	
	public abstract String UPER_DecodeBase64ToJson (String base64Msg);
	public abstract String UPER_DecodeHexToJson (String hexMsg);
	public abstract String UPER_DecodeBytesToJson (byte[] byteArrayMsg);
	
	public abstract String UPER_EncodeBase64 (Asn1Object asn1Object);
	public abstract String UPER_EncodeHex (Asn1Object asn1Object);
	public abstract byte[] UPER_EncodeBytes (Asn1Object asn1Object);
	
	public abstract String UPER_EncodeBase64FromJson (String asn1Object);
	public abstract String UPER_EncodeHexfromJson (String asn1Object);
	public abstract byte[] UPER_EncodeBytesFromJson (String asn1Object);
	
}
