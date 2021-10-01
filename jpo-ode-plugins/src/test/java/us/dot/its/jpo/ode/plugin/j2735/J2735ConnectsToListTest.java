package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class J2735ConnectsToListTest {
	@Mock
	J2735ConnectsToList connectionToList;
	
	@Mock
	J2735Connection connection;

	@Test
	public void testGettersSetters() {
		List<J2735Connection> connects = new ArrayList<J2735Connection>();
		connects.add(connection);
		connectionToList.setConnectsTo(connects);
		Mockito.when(connectionToList.getConnectsTo()).thenReturn(connects);
	}
}