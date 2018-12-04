package us.dot.its.jpo.ode.dds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import mockit.Mocked;
import us.dot.its.jpo.ode.dds.DdsRequest.Dialog;
import us.dot.its.jpo.ode.model.StatusTag;

public class DdsStatusMessageTest {
	
	@Test
	   public void testConstructor1() {
			DdsStatusMessage ddsStatusMessage = new DdsStatusMessage();
			
			assertNull(ddsStatusMessage.getTag());
			assertNull(ddsStatusMessage.getEncoding());
			assertNull(ddsStatusMessage.getDialog());
	}
	
	@Test
	   public void testConstructor2(@Mocked StatusTag tag, @Mocked Dialog dialog) {
			DdsStatusMessage ddsStatusMessage = new DdsStatusMessage(tag, "UPER", dialog);
			assertEquals(ddsStatusMessage.getTag(), tag);
			assertEquals(ddsStatusMessage.getEncoding(), "UPER");
			assertEquals(ddsStatusMessage.getDialog(), dialog);
			
			ddsStatusMessage.setTag(null);
			ddsStatusMessage.setDialog(null);
			ddsStatusMessage.setEncoding(null);
			
			assertNull(ddsStatusMessage.getTag());
			assertNull(ddsStatusMessage.getEncoding());
			assertNull(ddsStatusMessage.getDialog());
	}
	
	
	@Test
	   public void testRecordCount() {
			DdsStatusMessage ddsStatusMessage = new DdsStatusMessage();
			assertEquals(0, ddsStatusMessage.getRecordCount());
			ddsStatusMessage.setRecordCount(5);
			assertEquals(5, ddsStatusMessage.getRecordCount());
	}
	
	@Test
	   public void testConnectionDetails() {
			DdsStatusMessage ddsStatusMessage = new DdsStatusMessage();
			assertEquals(null, ddsStatusMessage.getConnectionDetails());
			ddsStatusMessage.setConnectionDetails("testConnectionString");
			assertEquals("testConnectionString", ddsStatusMessage.getConnectionDetails());
	}
	
	@Test
	   public void testToString() {
			StatusTag tag = StatusTag.CONNECTED;
			String encoding = "hex";
			Dialog dialog = Dialog.ASD;
			long recordCount = 3;
			String connectionDetails = "testConnectionString";
			DdsStatusMessage ddsStatusMessage = new DdsStatusMessage(tag, encoding, dialog);
			ddsStatusMessage.setConnectionDetails(connectionDetails);
			ddsStatusMessage.setRecordCount(recordCount);
			String ddsStatusMessageString = "DdsStatusMessage [tag=" + tag + ", encoding=" + encoding + ", dialog=" + dialog + ", recordCount="
		            + recordCount + ", connectionDetails=" + connectionDetails + "]";
			assertEquals(ddsStatusMessageString, ddsStatusMessage.toString());
	}
	
}
