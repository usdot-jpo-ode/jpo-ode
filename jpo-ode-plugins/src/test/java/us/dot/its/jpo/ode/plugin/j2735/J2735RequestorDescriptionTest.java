package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.codehaus.groovy.runtime.typehandling.BigDecimalMath;
import org.junit.jupiter.api.Test;

public class J2735RequestorDescriptionTest {
    
    @Test
	public void testGettersSetters() {
        J2735RequestorDescription requestorDescription = new J2735RequestorDescription();

        J2735VehicleID vehicleId = new J2735VehicleID();
        vehicleId.setEntityID("123");
        requestorDescription.setId(vehicleId);
        assertEquals(requestorDescription.getId().getEntityID(), "123");

        J2735RequestorType requestorType = new J2735RequestorType();
        requestorType.setRole(J2735BasicVehicleRole.emergency);
        requestorDescription.setType(requestorType);
        assertEquals(requestorDescription.getType().getRole(), J2735BasicVehicleRole.emergency);

        J2735RequestorPositionVector positionVector = new J2735RequestorPositionVector();
        positionVector.setHeading(BigDecimal.valueOf(5));
        requestorDescription.setPosition(positionVector);
        assertEquals(requestorDescription.getPosition().getHeading(), BigDecimal.valueOf(5));

        requestorDescription.setName("test");
        assertEquals(requestorDescription.getName(), "test");

        requestorDescription.setRouteName("test");
        assertEquals(requestorDescription.getRouteName(), "test");

        J2735BitString bitString = new J2735BitString();
        requestorDescription.setTransitStatus(bitString);
        assertEquals(requestorDescription.getTransitStatus(), bitString);

        requestorDescription.setTransitOccupancy(J2735TransitVehicleOccupancy.occupancyMed);
        assertEquals(requestorDescription.getTransitOccupancy(), J2735TransitVehicleOccupancy.occupancyMed);

        requestorDescription.setTransitSchedule(5);
        assertEquals(requestorDescription.getTransitSchedule(), 5);
	}
}
