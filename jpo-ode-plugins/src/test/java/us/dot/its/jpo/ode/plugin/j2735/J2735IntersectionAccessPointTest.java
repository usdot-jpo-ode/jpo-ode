package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class J2735IntersectionAccessPointTest {
    
    @Test
	public void testGettersSetters() {
        J2735IntersectionAccessPoint interAccessPoint = new J2735IntersectionAccessPoint();
        interAccessPoint.setApproach(5);
        assertEquals(interAccessPoint.getApproach(), 5);

        interAccessPoint.setConnection(4);
        assertEquals(interAccessPoint.getConnection(), 4);

        interAccessPoint.setLane(3);
        assertEquals(interAccessPoint.getLane(), 3);
	}
}
