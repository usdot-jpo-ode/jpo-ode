package us.dot.its.jpo.ode.plugin.j2735;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class J2735ConnectionTest {
	@Mock
	J2735Connection connection;
	
	@Mock
	J2735ConnectingLane connectionLane;
	
	@Mock
	J2735IntersectionReferenceID remoteIntersection;

	@Test
	public void testGettersSetters() {
		connectionLane.setLane(0);
		connectionLane.setManeuver(J2735AllowedManeuvers.caution);
		connection.setConnectingLane(connectionLane);
		
		connection.setSignalGroup(10);
		connection.setUserClass(10);
		connection.setConnectionID(10);
		remoteIntersection.setId(Integer.valueOf(10));
		remoteIntersection.setRegion(10);
		connection.setRemoteIntersection(remoteIntersection);
		
		Mockito.when(remoteIntersection.getId()).thenReturn(10);
		Mockito.when(remoteIntersection.getRegion()).thenReturn(10);
		Mockito.when(connection.getSignalGroup()).thenReturn(10);
	}
}
