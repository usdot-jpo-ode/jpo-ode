package us.dot.its.jpo.ode.pdm;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Tested;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.ServiceRequest;
import us.dot.its.jpo.ode.plugin.j2735.J2735ProbeDataManagment;

@RunWith(JMockit.class)
public class J2735PdmRequestTest {

    @Tested
    J2735PdmRequest testJ2735ProbeDataManagement;

    @Test
    public void testSetOdeAndGetOde() {

        int testOdeVersion = 3;

        ServiceRequest.OdeInternal testODE = new ServiceRequest.OdeInternal();
        testODE.setVersion(testOdeVersion);

        testJ2735ProbeDataManagement.setOde(testODE);
        assertEquals(testOdeVersion, testJ2735ProbeDataManagement.getOde().getVersion());
    }

    @Test
    public void testSetRsuListAndGetRsuList() {

        String testTarget = "testTarget123";

        RSU testRsu = new RSU();
        testRsu.setRsuTarget(testTarget);

        RSU[] testRsuList = new RSU[] { testRsu };

        testJ2735ProbeDataManagement.setRsuList(testRsuList);
        assertEquals(testTarget, testJ2735ProbeDataManagement.getRsuList()[0].getRsuTarget());
    }

    @Test
    public void testSetAndGetPdm() {

        int testDirections = 7;

        J2735ProbeDataManagment testPdm = new J2735ProbeDataManagment();
        testPdm.setDirections(testDirections);

        testJ2735ProbeDataManagement.setPdm(testPdm);
        assertEquals(testDirections, testJ2735ProbeDataManagement.getPdm().getDirections());
    }

}
