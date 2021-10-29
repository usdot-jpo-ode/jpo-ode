package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class J2735SignalRequesterInfoTest {
    
    @Test
	public void testGettersSetters() {
        J2735SignalRequesterInfo signalRequesterInfo = new J2735SignalRequesterInfo();
        
        J2735VehicleID vehicleId = new J2735VehicleID();
        vehicleId.setEntityID("test");
        signalRequesterInfo.setId(vehicleId);
        assertEquals(signalRequesterInfo.getId().getEntityID(), "test");

        signalRequesterInfo.setRequest(5);
        assertEquals(signalRequesterInfo.getRequest(), 5);

        signalRequesterInfo.setSequenceNumber(4);
        assertEquals(signalRequesterInfo.getSequenceNumber(), 4);

        signalRequesterInfo.setRole(J2735BasicVehicleRole.emergency);
        assertEquals(signalRequesterInfo.getRole(), J2735BasicVehicleRole.emergency);

        J2735RequestorType requestorType = new J2735RequestorType();
        requestorType.setRole(J2735BasicVehicleRole.emergency);
        signalRequesterInfo.setTypeData(requestorType);
        assertEquals(signalRequesterInfo.getTypeData().getRole(), J2735BasicVehicleRole.emergency);
	}
}
