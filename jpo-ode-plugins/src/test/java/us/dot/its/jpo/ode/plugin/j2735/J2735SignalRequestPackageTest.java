package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class J2735SignalRequestPackageTest {

    @Test
	public void testGettersSetters() {
		J2735SignalRequestPackage signalRequestPackage = new J2735SignalRequestPackage();

        J2735SignalRequest signalRequest = new J2735SignalRequest();
        signalRequest.setRequestID(5);
        signalRequestPackage.setRequest(signalRequest);
		assertEquals(signalRequestPackage.getRequest().getRequestID(), 5);

        signalRequestPackage.setMinute(5);
        assertEquals(signalRequestPackage.getMinute(), 5);

        signalRequestPackage.setSecond(5);
        assertEquals(signalRequestPackage.getSecond(), 5);

        signalRequestPackage.setDuration(5);
        assertEquals(signalRequestPackage.getDuration(), 5);
	}
}
