package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class J2735SignalRequestTest {
    
    @Test
	public void testGettersSetters() {
        J2735SignalRequest signalRequest = new J2735SignalRequest();
        
        J2735IntersectionReferenceID interRefId = new J2735IntersectionReferenceID();
        interRefId.setId(5);
        signalRequest.setId(interRefId);
        assertEquals(signalRequest.getId().getId(), 5);

        signalRequest.setRequestID(5);
        assertEquals(signalRequest.getRequestID(), 5);

        signalRequest.setRequestType(J2735PriorityRequestType.priorityRequest);
        assertEquals(signalRequest.getRequestType(), J2735PriorityRequestType.priorityRequest);

        J2735IntersectionAccessPoint interAccessPoint = new J2735IntersectionAccessPoint();
        interAccessPoint.setApproach(5);
        signalRequest.setInBoundLane(interAccessPoint);
        assertEquals(signalRequest.getInBoundLane().getApproach(), 5);

        signalRequest.setOutBoundLane(interAccessPoint);
        assertEquals(signalRequest.getOutBoundLane().getApproach(), 5);
	}
}
