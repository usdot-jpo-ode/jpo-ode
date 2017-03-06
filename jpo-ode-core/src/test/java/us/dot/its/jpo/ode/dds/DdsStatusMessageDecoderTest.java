package us.dot.its.jpo.ode.dds;

import static org.junit.Assert.*;

import org.junit.Test;

import us.dot.its.jpo.ode.model.StatusTag;

public class DdsStatusMessageDecoderTest {

	@Test
	public void TestGetResponseTag() {
		assertEquals(StatusTag.CLOSED, DdsStatusMessageDecoder.getResponseTag("CLOSED"));
		assertEquals(StatusTag.DEPOSITED, DdsStatusMessageDecoder.getResponseTag("DEPOSITED"));
		assertNotEquals(StatusTag.DEPOSITED, DdsStatusMessageDecoder.getResponseTag("OPENED"));
		assertNull(DdsStatusMessageDecoder.getResponseTag("WRONT_TAG"));
	}
}
