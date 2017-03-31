package us.dot.its.jpo.ode.dds;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import us.dot.its.jpo.ode.dds.DdsRequest.Dialog;
import us.dot.its.jpo.ode.dds.DdsRequest.EncodeType;
import us.dot.its.jpo.ode.dds.DdsRequest.SystemName;

public class DdsRequestTest {
	
	private DdsRequest ddsRequest;
	
	@Before
	public void setUp() throws Exception {
		ddsRequest = new DdsRequest();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDialogEnum() {
		Dialog dialog = Dialog.ASD;
		
		assertEquals(Dialog.getById(162), Dialog.ISD);
		assertEquals(dialog.getId(), 156);
		assertNull(Dialog.getById(999));
	}
	
	@Test
	public void testSystemNameEnum() {
		SystemName systemName = SystemName.SDC;
		assertEquals(systemName.getName(), "SDC 2.3");
	}
	
	@Test
	public void testDialogId() {
		ddsRequest.setDialogID(156);
		assertEquals(156, ddsRequest.getDialogID());
	}
	
	@Test
	public void testResultEncoding() {
		String encodingType = "hex";
		ddsRequest.setResultEncoding(encodingType);
		assertEquals(encodingType, ddsRequest.getResultEncoding());
	}
	
	@Test
	public void testHashCode() {
		int hashCode1, hashCode2;
		DdsRequest ddsRequest2 = new DdsRequest();
		hashCode1 = ddsRequest.hashCode();
		hashCode2 = ddsRequest2.hashCode();
		assertEquals(hashCode1, hashCode2);
		
		ddsRequest2.setResultEncoding("hex");
		
		hashCode1 = ddsRequest.hashCode();
		hashCode2 = ddsRequest2.hashCode();
		assertNotEquals(hashCode1, hashCode2);
		
		ddsRequest.setResultEncoding("hex");
		
		hashCode1 = ddsRequest.hashCode();
		hashCode2 = ddsRequest2.hashCode();
		assertEquals(hashCode1, hashCode2);
		
		ddsRequest2.setDialogID(156);
		
		hashCode1 = ddsRequest.hashCode();
		hashCode2 = ddsRequest2.hashCode();
		assertNotEquals(hashCode1, hashCode2);
		
		ddsRequest.setDialogID(156);
		
		hashCode1 = ddsRequest.hashCode();
		hashCode2 = ddsRequest2.hashCode();
		assertEquals(hashCode1, hashCode2);
	}
	
	@Test
	public void testEquals() {
		EncodeType encodeType = EncodeType.HEX;
		DdsRequest ddsRequest2 = new DdsRequest();
		assertTrue(ddsRequest.equals(ddsRequest2));

		ddsRequest2.setResultEncoding(encodeType.name());
		assertFalse(ddsRequest.equals(ddsRequest2));
		
		ddsRequest.setResultEncoding(encodeType.name());
		assertTrue(ddsRequest.equals(ddsRequest2));
		
		ddsRequest2.setDialogID(156);
		assertFalse(ddsRequest.equals(ddsRequest2));
		
		ddsRequest.setDialogID(156);
		assertTrue(ddsRequest.equals(ddsRequest2));
		assertTrue(ddsRequest.equals(ddsRequest));
		
		ddsRequest2.setResultEncoding(EncodeType.BASE64.name());
		assertFalse(ddsRequest.equals(ddsRequest2));
	}
}
