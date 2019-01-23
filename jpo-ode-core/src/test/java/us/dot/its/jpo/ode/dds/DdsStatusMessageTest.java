/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode.dds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import mockit.Mocked;
import us.dot.its.jpo.ode.dds.DdsRequest.Dialog;
import us.dot.its.jpo.ode.model.StatusTag;

public class DdsStatusMessageTest {
	
	private static final String UPER = "UPER";

  @Test
	   public void testConstructor1() {
			DdsStatusMessage ddsStatusMessage = new DdsStatusMessage();
			
			assertNull(ddsStatusMessage.getTag());
			assertNull(ddsStatusMessage.getEncoding());
			assertNull(ddsStatusMessage.getDialog());
	}
	
	@Test
	   public void testConstructor2(@Mocked StatusTag tag, @Mocked Dialog dialog) {
			DdsStatusMessage ddsStatusMessage = new DdsStatusMessage(tag, UPER, dialog);
			assertEquals(ddsStatusMessage.getTag(), tag);
			assertEquals(UPER, ddsStatusMessage.getEncoding());
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
