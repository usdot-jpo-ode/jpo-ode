package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class J2735SignalStatusPackageTest {
    
    @Test
	public void testGettersSetters() {
        J2735SignalStatusPackage signalStatusPackage = new J2735SignalStatusPackage();

        J2735SignalRequesterInfo signalRequesterInfo = new J2735SignalRequesterInfo();
        signalRequesterInfo.setRequest(5);
        signalStatusPackage.setRequester(signalRequesterInfo);
        assertEquals(signalStatusPackage.getRequester().getRequest(), 5);

        J2735IntersectionAccessPoint intersectionAccessPoint = new J2735IntersectionAccessPoint();
        intersectionAccessPoint.setApproach(5);
        signalStatusPackage.setInboundOn(intersectionAccessPoint);
        assertEquals(signalStatusPackage.getInboundOn().getApproach(), 5);

        signalStatusPackage.setOutboundOn(intersectionAccessPoint);
        assertEquals(signalStatusPackage.getOutboundOn().getApproach(), 5);

        signalStatusPackage.setMinute(5);
        assertEquals(signalStatusPackage.getMinute(), 5);

        signalStatusPackage.setSecond(4);
        assertEquals(signalStatusPackage.getSecond(), 4);

        signalStatusPackage.setDuration(3);
        assertEquals(signalStatusPackage.getDuration(), 3);

        signalStatusPackage.setStatus(J2735PrioritizationResponseStatus.granted);
        assertEquals(signalStatusPackage.getStatus(), J2735PrioritizationResponseStatus.granted);
	}
}
