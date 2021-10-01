package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class J2735ConnectsToListTest {
	
	@Test
	public void testGettersSetters() {
		J2735ConnectsToList connectionToList = new J2735ConnectsToList();
		J2735Connection connection = new J2735Connection();
		List<J2735Connection> connects = new ArrayList<J2735Connection>();
		connects.add(connection);
		connectionToList.setConnectsTo(connects);
		assertEquals(connectionToList.getConnectsTo(),connects);
	}
}