package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class J2735RequestorPositionVectorTest {
    
    @Test
	public void testGettersSetters() {
        J2735RequestorPositionVector requestorPositionVector = new J2735RequestorPositionVector();
        
        OdePosition3D pos3d = new OdePosition3D();
        pos3d.setElevation(BigDecimal.valueOf(105));
        requestorPositionVector.setPosition(pos3d);
        assertEquals(requestorPositionVector.getPosition().getElevation(), BigDecimal.valueOf(105));

        requestorPositionVector.setHeading(BigDecimal.valueOf(5));
        assertEquals(requestorPositionVector.getHeading(), BigDecimal.valueOf(5));

        J2735TransmissionAndSpeed speed = new J2735TransmissionAndSpeed();
        speed.setSpeed(BigDecimal.valueOf(105));
        requestorPositionVector.setSpeed(speed);
        assertEquals(requestorPositionVector.getSpeed().getSpeed(), BigDecimal.valueOf(105));
	}
}
