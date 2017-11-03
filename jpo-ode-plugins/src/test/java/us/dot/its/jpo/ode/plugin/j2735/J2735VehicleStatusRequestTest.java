package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Tested;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleStatusRequest;

@RunWith(JMockit.class)
public class J2735VehicleStatusRequestTest {

    @Tested
    J2735VehicleStatusRequest testVehicleStatusRequest;

    @Test
    public void testSettersAndGetters() {

        int testLessThenValue = 62;
        testVehicleStatusRequest.setLessThenValue(testLessThenValue);
        assertEquals(testLessThenValue, testVehicleStatusRequest.getLessThenValue());

        int testMoreThenValue = 64;
        testVehicleStatusRequest.setMoreThenValue(testMoreThenValue);
        assertEquals(testMoreThenValue, testVehicleStatusRequest.getMoreThenValue());

        int testSendAll = 66;
        testVehicleStatusRequest.setSendAll(testSendAll);
        assertEquals(testSendAll, testVehicleStatusRequest.getSendAll());

        int testStatus = 68;
        testVehicleStatusRequest.setStatus(testStatus);
        assertEquals(testStatus, testVehicleStatusRequest.getStatus());

        int testSubTag = 70;
        testVehicleStatusRequest.setSubTag(testSubTag);
        assertEquals(testSubTag, testVehicleStatusRequest.getSubTag());

        int testTag = 72;
        testVehicleStatusRequest.setTag(testTag);
        assertEquals(testTag, testVehicleStatusRequest.getTag());
    }

}
