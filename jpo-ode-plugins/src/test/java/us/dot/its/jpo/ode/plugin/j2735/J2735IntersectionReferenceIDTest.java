package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class J2735IntersectionReferenceIDTest {
    
    @Test
	public void testGettersSetters() {
        J2735IntersectionReferenceID interRefId = new J2735IntersectionReferenceID();
        interRefId.setId(5);
        assertEquals(interRefId.getId(), 5);

        interRefId.setRegion(4);
        assertEquals(interRefId.getRegion(), 4);
	}
}
