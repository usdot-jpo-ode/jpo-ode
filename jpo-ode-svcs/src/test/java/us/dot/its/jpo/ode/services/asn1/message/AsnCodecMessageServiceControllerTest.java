package us.dot.its.jpo.ode.services.asn1.message;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;

import us.dot.its.jpo.ode.OdeProperties;
	
public class AsnCodecMessageServiceControllerTest {

	@Test
	public void shouldStartTwoConsumers() {
		OdeProperties odeProps = new OdeProperties();
		odeProps.setKafkaBrokers("localhost:9092");
		AsnCodecMessageServiceController asnCodecMessageServiceController = new AsnCodecMessageServiceController(odeProps);
		assertNotNull(asnCodecMessageServiceController);
	}

}
