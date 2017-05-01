package us.dot.its.jpo.ode.common.asn1;

import us.dot.its.jpo.ode.j2735.semi.GroupID;

import java.nio.ByteBuffer;

public class GroupIDHelper {
	
	static public GroupID toGroupID(int groupID) {
		byte[] bytes = ByteBuffer.allocate(4).putInt(groupID).array();
		return new GroupID(bytes);
	}
	
	static public int fromGroupID(GroupID groupID) {
		return ByteBuffer.wrap(groupID.byteArrayValue()).getInt();
	}
}
