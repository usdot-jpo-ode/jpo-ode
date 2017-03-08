package us.dot.its.jpo.ode.dds;

import static org.junit.Assert.*;

import javax.websocket.DecodeException;

import org.junit.Test;

public class DepositResponseDecoderTest {

	@Test
	public void testWillDecode() {
		DepositResponseDecoder depositResponseDecoder = new DepositResponseDecoder();
		assertTrue(depositResponseDecoder.willDecode("Any String"));
		assertTrue(depositResponseDecoder.willDecode("Always returns true"));
	}
	
	@Test
	public void testDecode() {
		DepositResponseDecoder depositResponseDecoder = new DepositResponseDecoder();
		DdsMessage ddsMessage;
		try {
			ddsMessage = depositResponseDecoder.decode("DEPOSITED:1");
			String expectedDdsMessage = "DdsStatusMessage [tag=DEPOSITED, encoding=null, dialog=null, recordCount=1, connectionDetails=null]";
			assertEquals(ddsMessage.toString(), expectedDdsMessage);
			
			ddsMessage = depositResponseDecoder.decode("CONNECTED:testConnectionDetail");
			expectedDdsMessage = "DdsStatusMessage [tag=CONNECTED, encoding=null, dialog=null, recordCount=0, connectionDetails=testConnectionDetail]";
			assertEquals(ddsMessage.toString(), expectedDdsMessage);
			
			ddsMessage = depositResponseDecoder.decode("START:{\"dialogID\":156, \"resultEncoding\":\"hex\"}");
			expectedDdsMessage = "DdsStatusMessage [tag=START, encoding=hex, dialog=ASD, recordCount=0, connectionDetails=null]";
			assertEquals(ddsMessage.toString(), expectedDdsMessage);
			
			ddsMessage = depositResponseDecoder.decode("STOP:recordCount=2");
			expectedDdsMessage = "DdsStatusMessage [tag=STOP, encoding=null, dialog=null, recordCount=2, connectionDetails=null]";
			assertEquals(ddsMessage.toString(), expectedDdsMessage);
			
			ddsMessage = depositResponseDecoder.decode("ERROR:sampleError");
			expectedDdsMessage = "DdsStatusMessage [tag=ERROR, encoding=null, dialog=null, recordCount=0, connectionDetails=null]";
			assertEquals(ddsMessage.toString(), expectedDdsMessage);
			
		} catch (DecodeException e) {
			e.printStackTrace();
		}	
	}

}
