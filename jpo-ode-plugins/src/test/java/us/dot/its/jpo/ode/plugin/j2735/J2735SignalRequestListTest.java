package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class J2735SignalRequestListTest {
    
    @Test
	public void testGettersSetters() {
		J2735SignalRequestList signalRequestList = new J2735SignalRequestList();
		J2735SignalRequestPackage signalRequestPackage = new J2735SignalRequestPackage();
		List<J2735SignalRequestPackage> signalRequestPackageList = new ArrayList<J2735SignalRequestPackage>();
		signalRequestPackageList.add(signalRequestPackage);
		signalRequestList.setRequests(signalRequestPackageList);
		assertEquals(signalRequestList.getRequests(),signalRequestPackageList);
	}
}
