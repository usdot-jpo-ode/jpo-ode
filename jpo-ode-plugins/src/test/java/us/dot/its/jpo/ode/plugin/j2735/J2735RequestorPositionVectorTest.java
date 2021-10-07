package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class J2735RequestorPositionVectorTest {
    
    @Test
	public void testGettersSetters() {
        J2735RequestorPositionVector requestorPositionVector = new J2735RequestorPositionVector();
        
        J2735Position3D pos3d = new J2735Position3D();
        pos3d.setElevation(105);
        requestorPositionVector.setPosition(pos3d);
        assertEquals(requestorPositionVector.getPosition().getElevation(), 105);

        requestorPositionVector.setHeading(5);
        assertEquals(requestorPositionVector.getHeading(), 5);

        J2735TransmissionAndSpeed speed = new J2735TransmissionAndSpeed();
        speed.setSpeed(BigDecimal.valueOf(105L));
        requestorPositionVector.setSpeed(speed);
        assertEquals(requestorPositionVector.getSpeed().getSpeed(), BigDecimal.valueOf(105L));
	}
}
