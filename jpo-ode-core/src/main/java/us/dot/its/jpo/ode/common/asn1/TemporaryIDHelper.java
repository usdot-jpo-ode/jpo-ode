package us.dot.its.jpo.ode.common.asn1;

import us.dot.its.jpo.ode.j2735.dsrc.TemporaryID;

import java.nio.ByteBuffer;

public class TemporaryIDHelper {
	
	static public TemporaryID toTemporaryID(int temporaryID) {
		byte[] bytes = ByteBuffer.allocate(4).putInt(temporaryID).array();
		return new TemporaryID(bytes);
	}
	
	static public int fromTemporaryID(TemporaryID temporaryId) {
		return ByteBuffer.wrap(temporaryId.byteArrayValue()).getInt();
	}
}
