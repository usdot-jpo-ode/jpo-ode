package us.dot.its.jpo.ode.services.asn1.message;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AsnCodecMessageServiceControllerTest {

	@Mock
	AsnCodecMessageServiceController asnCodecMessageServiceController;

	@Test
	public void shouldStartTwoConsumers() {

		assertNotNull(asnCodecMessageServiceController);
	}

}
