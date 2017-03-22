package us.dot.its.jpo.ode.plugin.j2735.pdm;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Tested;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.j2735.J2735TravelerInputData.ODE;

@RunWith(JMockit.class)
public class J2735ProbeDataManagementTest {

    @Tested
    J2735ProbeDataManagement testJ2735ProbeDataManagement;

    @Test
    public void testSetOdeAndGetOde() {

        int testOdeVersion = 3;

        ODE testODE = new ODE();
        testODE.setVersion(testOdeVersion);

        testJ2735ProbeDataManagement.setOde(testODE);
        assertEquals(testOdeVersion, testJ2735ProbeDataManagement.getOde().getVersion());
    }

    @Test
    public void testSetRsuListAndGetRsuList() {

        String testTarget = "testTarget123";

        RSU testRsu = new RSU();
        testRsu.setrsuTarget(testTarget);

        RSU[] testRsuList = new RSU[] { testRsu };

        testJ2735ProbeDataManagement.setRsuList(testRsuList);
        assertEquals(testTarget, testJ2735ProbeDataManagement.getRsuList()[0].getrsuTarget());
    }

    @Test
    public void testSetAndGetPdm() {

        int testDirections = 7;

        PDM testPdm = new PDM();
        testPdm.setDirections(testDirections);

        testJ2735ProbeDataManagement.setPdm(testPdm);
        assertEquals(testDirections, testJ2735ProbeDataManagement.getPdm().getDirections());
    }

}
