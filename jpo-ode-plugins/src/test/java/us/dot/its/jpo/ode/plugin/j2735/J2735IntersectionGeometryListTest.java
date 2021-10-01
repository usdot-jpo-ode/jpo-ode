package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class J2735IntersectionGeometryListTest {

	@Test
	public void testGettersSetters() {
		J2735IntersectionGeometryList intersectionGeometryList = new J2735IntersectionGeometryList();
		J2735IntersectionGeometry intersectionGeometry = new J2735IntersectionGeometry();
		List<J2735IntersectionGeometry> geometryList = new ArrayList<J2735IntersectionGeometry>();
		geometryList.add(intersectionGeometry);
		intersectionGeometryList.setIntersections(geometryList);
		assertEquals(intersectionGeometryList.getIntersections(),geometryList);
	}
}

