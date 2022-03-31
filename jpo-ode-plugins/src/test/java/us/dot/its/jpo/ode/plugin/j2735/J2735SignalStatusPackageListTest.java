package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class J2735SignalStatusPackageListTest {
    
    @Test
	public void testGettersSetters() {
		J2735SignalStatusPackageList signalStatusPackageList = new J2735SignalStatusPackageList();
		J2735SignalStatusPackage signalStatusPackage = new J2735SignalStatusPackage();
		List<J2735SignalStatusPackage> signalStatusPackageListObj = new ArrayList<J2735SignalStatusPackage>();
		signalStatusPackageListObj.add(signalStatusPackage);
		signalStatusPackageList.setSigStatus(signalStatusPackageListObj);
		assertEquals(signalStatusPackageList.getSigStatus(),signalStatusPackageListObj);
	}
}
