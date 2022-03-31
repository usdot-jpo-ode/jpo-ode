/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode.pdm;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Tested;
//import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.ServiceRequest;
import us.dot.its.jpo.ode.plugin.j2735.J2735ProbeDataManagment;

//@RunWith(JMockit.class)
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
