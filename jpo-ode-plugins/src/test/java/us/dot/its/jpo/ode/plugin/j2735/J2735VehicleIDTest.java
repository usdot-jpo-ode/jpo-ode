package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class J2735VehicleIDTest {
    
    @Test
	public void testGettersSetters() {
        J2735VehicleID vehicleId = new J2735VehicleID();
        vehicleId.setEntityID("test");
        vehicleId.setStationID(105L);
        assertEquals(vehicleId.getEntityID(), "test");
        assertEquals(vehicleId.getStationID(), 105L);
	}
}
