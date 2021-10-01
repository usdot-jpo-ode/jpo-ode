package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class J2735IntersectionGeometryListTest {
	@Mock
	J2735IntersectionGeometryList intersectionGeometryList;
	
	@Mock
	J2735IntersectionGeometry intersectionGeometry;

	@Test
	public void testGettersSetters() {
		List<J2735IntersectionGeometry> geometryList = new ArrayList<J2735IntersectionGeometry>();
		geometryList.add(intersectionGeometry);
		intersectionGeometryList.setIntersections(geometryList);
		Mockito.when(intersectionGeometryList.getIntersections()).thenReturn(geometryList);
	}
}

