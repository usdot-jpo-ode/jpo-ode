package us.dot.its.jpo.ode.dds;

import static org.junit.Assert.*;

import javax.websocket.DecodeException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import us.dot.its.jpo.ode.model.StatusTag;

public class DdsStatusMessageDecoderTest {

	@Test
	public void testGetResponseTag() {
		assertEquals(StatusTag.CLOSED, DdsStatusMessageDecoder.getResponseTag("CLOSED"));
		assertEquals(StatusTag.DEPOSITED, DdsStatusMessageDecoder.getResponseTag("DEPOSITED"));
		assertNotEquals(StatusTag.DEPOSITED, DdsStatusMessageDecoder.getResponseTag("OPENED"));
		assertNull(DdsStatusMessageDecoder.getResponseTag("WRONT_TAG"));
	}
	
	@Test
	public void testParseFullMsg() {
		DdsStatusMessageDecoder ddsStatusMessageDecoder = new DdsStatusMessageDecoder();
		String msg = "DEPOSITED:1";
		String[] msgs = ddsStatusMessageDecoder.parseFullMsg(msg);
		assertEquals("DEPOSITED", msgs[0]);
		assertEquals("1", msgs[1]);
	}
	
	@Test
	public void testWillDecode() {
		DdsStatusMessageDecoder ddsStatusMessageDecoder = new DdsStatusMessageDecoder();
		assertFalse(ddsStatusMessageDecoder.willDecode("DEPOSITED:1"));
		assertTrue(ddsStatusMessageDecoder.willDecode("START:1"));
	}
	
	@Test
	public void testDecode() {
		DdsStatusMessageDecoder ddsStatusMessageDecoder = new DdsStatusMessageDecoder();
		DdsMessage ddsMessage;
		try {
			ddsMessage = ddsStatusMessageDecoder.decode("DEPOSITED:1");
			String expectedDdsMessage = "DdsStatusMessage [tag=DEPOSITED, encoding=null, dialog=null, recordCount=1, connectionDetails=null]";
			assertEquals(ddsMessage.toString(), expectedDdsMessage);
			
			ddsMessage = ddsStatusMessageDecoder.decode("CONNECTED:testConnectionDetail");
			expectedDdsMessage = "DdsStatusMessage [tag=CONNECTED, encoding=null, dialog=null, recordCount=0, connectionDetails=testConnectionDetail]";
			assertEquals(ddsMessage.toString(), expectedDdsMessage);
			
			ddsMessage = ddsStatusMessageDecoder.decode("START:{\"dialogID\":156, \"resultEncoding\":\"hex\"}");
			expectedDdsMessage = "DdsStatusMessage [tag=START, encoding=hex, dialog=ASD, recordCount=0, connectionDetails=null]";
			assertEquals(ddsMessage.toString(), expectedDdsMessage);
			
			ddsMessage = ddsStatusMessageDecoder.decode("STOP:recordCount=2");
			expectedDdsMessage = "DdsStatusMessage [tag=STOP, encoding=null, dialog=null, recordCount=2, connectionDetails=null]";
			assertEquals(ddsMessage.toString(), expectedDdsMessage);
			
			ddsMessage = ddsStatusMessageDecoder.decode("ERROR:sampleError");
			expectedDdsMessage = "DdsStatusMessage [tag=ERROR, encoding=null, dialog=null, recordCount=0, connectionDetails=null]";
			assertEquals(ddsMessage.toString(), expectedDdsMessage);
			
		} catch (DecodeException e) {
			e.printStackTrace();
		}	
	}
}
