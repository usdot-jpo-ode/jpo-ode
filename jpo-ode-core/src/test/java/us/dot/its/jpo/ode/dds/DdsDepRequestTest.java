package us.dot.its.jpo.ode.dds;

import static org.junit.Assert.*;

import org.junit.Test;

public class DdsDepRequestTest {

	@Test
	public void testSystemDepositName() {
		DdsDepRequest ddsDepRequest = new DdsDepRequest();
		String depositName = "test deposit name";
		ddsDepRequest.setSystemDepositName(depositName);
		assertEquals(depositName, ddsDepRequest.getSystemDepositName());
	}
	
	@Test
	public void testEncodeType() {
		DdsDepRequest ddsDepRequest = new DdsDepRequest();
		String encodeType = "hex";
		ddsDepRequest.setEncodeType(encodeType);
		assertEquals(encodeType, ddsDepRequest.getEncodeType());
		assertNotEquals("base64", ddsDepRequest.getEncodeType());
	}
	
	@Test
	public void testEncodedMsg() {
		DdsDepRequest ddsDepRequest = new DdsDepRequest();
		String encodedMsg = "Sample Encode Msg";
		ddsDepRequest.setEncodedMsg(encodedMsg);
		assertEquals(encodedMsg, ddsDepRequest.getEncodedMsg());
		assertNotEquals("invalid encoded msg", ddsDepRequest.getEncodedMsg());
	}
	
	@Test
	public void testToString() {
		DdsDepRequest ddsDepRequest = new DdsDepRequest();
		String encodedMsg = "testEncodeMsg";
		String encodeType = "hex";
		String depositName = "testDepositName";
		
		ddsDepRequest.setEncodeType(encodedMsg);
		ddsDepRequest.setEncodeType(encodeType);
		ddsDepRequest.setSystemDepositName(depositName);
		String expectedStr = "DEPOSIT:{\"systemDepositName\":\"testDepositName\",\"encodeType\":\"hex\",\"dialogID\":0}";
		assertEquals(ddsDepRequest.toString(), expectedStr);
	}
	
	@Test
	public void testHashCode() {
		DdsDepRequest ddsDepRequest1 = new DdsDepRequest();
		DdsDepRequest ddsDepRequest2 = new DdsDepRequest();
		assertEquals(ddsDepRequest1.hashCode(), ddsDepRequest2.hashCode());
		
		String encodeType = "hex";
		ddsDepRequest1.setEncodeType(encodeType);
		assertNotEquals(ddsDepRequest1.hashCode(), ddsDepRequest2.hashCode());
		
		ddsDepRequest2.setEncodeType(encodeType);
		assertEquals(ddsDepRequest1.hashCode(), ddsDepRequest2.hashCode());
		
		String encodedMsg = "testEncodeMsg";
		ddsDepRequest1.setEncodedMsg(encodedMsg);
		ddsDepRequest2.setEncodedMsg(encodedMsg);
		
		String depositName = "testDepositName";
		ddsDepRequest1.setSystemDepositName(depositName);
		ddsDepRequest2.setSystemDepositName(depositName);
		
		assertEquals(ddsDepRequest1.hashCode(), ddsDepRequest2.hashCode());
	}
	
	@Test
	public void testEquals() {
		DdsDepRequest ddsDepRequest1 = new DdsDepRequest();
		DdsDepRequest ddsDepRequest2 = new DdsDepRequest();
		Object randomObject = new Object();
		assertTrue(ddsDepRequest1.equals(ddsDepRequest2));
		assertTrue(ddsDepRequest1.equals(ddsDepRequest1));
		assertFalse(ddsDepRequest1.equals(randomObject));
		assertFalse(ddsDepRequest1.equals(null));
		
		ddsDepRequest2.setEncodeType("NotNull");
		assertFalse(ddsDepRequest1.equals(ddsDepRequest2));
		
		String encodeType = "hex";
		ddsDepRequest1.setEncodeType(encodeType);
		assertFalse(ddsDepRequest1.equals(ddsDepRequest2));
		
		ddsDepRequest2.setEncodeType(encodeType);
		assertTrue(ddsDepRequest1.equals(ddsDepRequest2));
		
		ddsDepRequest2.setEncodedMsg("NotNull");
		assertFalse(ddsDepRequest1.equals(ddsDepRequest2));
		
		String encodedMsg = "testEncodeMsg";
		ddsDepRequest1.setEncodedMsg(encodedMsg);
		assertFalse(ddsDepRequest1.equals(ddsDepRequest2));
		
		ddsDepRequest2.setEncodedMsg(encodedMsg);
		assertTrue(ddsDepRequest1.equals(ddsDepRequest2));
		
		ddsDepRequest2.setSystemDepositName("NotNull");
		assertFalse(ddsDepRequest1.equals(ddsDepRequest2));
		
		String depositName = "testDepositName";
		ddsDepRequest1.setSystemDepositName(depositName);
		assertFalse(ddsDepRequest1.equals(ddsDepRequest2));
		
		ddsDepRequest2.setSystemDepositName(depositName);
		assertTrue(ddsDepRequest1.equals(ddsDepRequest2));
	}
	
	

}
