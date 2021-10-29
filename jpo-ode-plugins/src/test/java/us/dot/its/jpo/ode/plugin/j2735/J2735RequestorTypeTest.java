package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class J2735RequestorTypeTest {
    
    @Test
	public void testGettersSetters() {
        J2735RequestorType requestorType = new J2735RequestorType();
        
        requestorType.setRole(J2735BasicVehicleRole.fire);
        assertEquals(requestorType.getRole(), J2735BasicVehicleRole.fire);

        requestorType.setSubrole(J2735RequestSubRole.requestSubRole1);
        assertEquals(requestorType.getSubrole(), J2735RequestSubRole.requestSubRole1);

        requestorType.setRequest(J2735RequestImportanceLevel.requestImportanceLevel1);
        assertEquals(requestorType.getRequest(), J2735RequestImportanceLevel.requestImportanceLevel1);

        requestorType.setIso3883(5);
        assertEquals(requestorType.getIso3883(), 5);

        requestorType.setHpmsType(J2735VehicleType.car);
        assertEquals(requestorType.getHpmsType(), J2735VehicleType.car);
	}
}
