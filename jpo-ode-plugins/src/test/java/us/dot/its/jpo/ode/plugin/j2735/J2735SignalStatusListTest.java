package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class J2735SignalStatusListTest {
    
    @Test
	public void testGettersSetters() {
		J2735SignalStatusList signalStatusList = new J2735SignalStatusList();
		J2735SignalStatus signalStatus = new J2735SignalStatus();
		List<J2735SignalStatus> signalStatusListObj = new ArrayList<J2735SignalStatus>();
		signalStatusListObj.add(signalStatus);
		signalStatusList.setStatus(signalStatusListObj);
		assertEquals(signalStatusList.getStatus(),signalStatusListObj);
	}
}
