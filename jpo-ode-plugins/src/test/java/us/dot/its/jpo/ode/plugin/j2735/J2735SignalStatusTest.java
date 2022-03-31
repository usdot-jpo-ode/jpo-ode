package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class J2735SignalStatusTest {
    
    @Test
	public void testGettersSetters() {
        J2735SignalStatus signalStatus = new J2735SignalStatus();

        signalStatus.setSequenceNumber(5);
        assertEquals(signalStatus.getSequenceNumber(), 5);

        J2735IntersectionReferenceID interRefId = new J2735IntersectionReferenceID();
        interRefId.setId(5);
        signalStatus.setId(interRefId);
        assertEquals(signalStatus.getId().getId(), 5);

        J2735SignalStatusPackageList sigStatusList = new J2735SignalStatusPackageList();
        J2735SignalStatusPackage sigPackage = new J2735SignalStatusPackage();
        List<J2735SignalStatusPackage> sigStatusListObj = new ArrayList<J2735SignalStatusPackage>();
        sigStatusListObj.add(sigPackage);
        sigStatusList.setSigStatus(sigStatusListObj);
        signalStatus.setSigStatus(sigStatusList);
        assertEquals(signalStatus.getSigStatus(), sigStatusList);
	}
}
