package us.dot.its.jpo.ode.vsdm;

import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Ignore;

import us.dot.its.jpo.ode.j2735.dsrc.MsgCRC;
import us.dot.its.jpo.ode.j2735.semi.GroupID;
import us.dot.its.jpo.ode.j2735.semi.SemiDialogID;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage.Bundle;

public class VsdToBsmConverterTest {
	
	@Ignore @Before
	public void setUp() {
		fail("Not yet implemented");
		
		VehSitDataMessage vsdm = new VehSitDataMessage();
		vsdm.bundle = new Bundle();
		vsdm.bundle.elements = new ArrayList<>();
		vsdm.crc = new MsgCRC(new byte[]{1});
		vsdm.dialogID = new SemiDialogID(2);
		vsdm.groupID = new GroupID(new byte[]{3});
		
		//TODO
		
	}

}
