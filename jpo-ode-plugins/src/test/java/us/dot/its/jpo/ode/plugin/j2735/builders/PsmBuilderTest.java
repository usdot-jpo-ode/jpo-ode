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
package us.dot.its.jpo.ode.plugin.j2735.builders;

import static org.junit.Assert.*;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735PSM;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class PsmBuilderTest {

   @Test
   public void shouldTranslatePSM() {

      JsonNode jsonPsm = null;
      try {
         jsonPsm = XmlUtils.toObjectNode(
               "<OdeAsn1Data><payload><dataType>MessageFrame</dataType><data><MessageFrame><messageld>32</messageld><value><PersonalSafetyMessage><basicType><aPEDESTRIAN/></basicType><secMark>10064</secMark><msgCnt>55</msgCnt><id>24 77 9D 7E</id><position><lat>402397634</lat><long>-742761531</long></position><accuracy><semiMajor>20</semiMajor><semiMinor>20</semiMinor><orientation>5000</orientation></accuracy><speed>0</speed><heading>27553</heading></PersonalSafetyMessage></value></MessageFrame></data></payload></OdeAsn1Data>");
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      J2735PSM actualPsm = PSMBuilder.genericPSM(jsonPsm.findValue("PersonalSafetyMessage"));

      assertNotNull(actualPsm);
      String expected = "{\"basicType\":\"aPEDESTRIAN\",\"secMark\":10064,\"msgCnt\":55,\"id\":\"24779D7E\",\"position\":{\"latitude\":40.2397634,\"longitude\":-74.2761531},\"accuracy\":{\"semiMajor\":1.00,\"semiMinor\":1.00,\"orientation\":27.4662395000},\"speed\":0,\"heading\":27553}";
      assertEquals(expected , actualPsm.toString());
   }

}
