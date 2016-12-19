package us.dot.its.jpo.ode.plugin.j2735.oss;

import com.oss.asn1.BitString;

import us.dot.its.jpo.ode.plugin.j2735.J2735BitString;

public class OssBitString {

	public static J2735BitString genericBitString(BitString bitString) {
		J2735BitString genericBitString = new J2735BitString();
		
		for (int i = 0; i < bitString.getSize(); i++) {
			genericBitString.put(
					bitString.getTypeName(), 
					bitString.getBit(i));
		}
		
		return genericBitString;
	}
}
