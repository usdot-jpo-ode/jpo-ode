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
               "<OdeAsn1Data><payload><dataType>MessageFrame</dataType><data><MessageFrame><messageId>32</messageId><value><PersonalSafetyMessage><basicType><aPEDESTRIAN/></basicType><secMark>3564</secMark><msgCnt>26</msgCnt><id>24779D7E</id><position><lat>402397377</lat><long>-742761437</long></position><accuracy><semiMajor>20</semiMajor><semiMinor>20</semiMinor><orientation>8191</orientation></accuracy><speed>0</speed><heading>8898</heading></PersonalSafetyMessage></value></MessageFrame></data></payload></OdeAsn1Data>");
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      J2735PSM actualPsm = PSMBuilder.genericPSM(jsonPsm.findValue("PersonalSafetyMessage"));

      assertNotNull(actualPsm);
      String expected = "{\"basicType\":\"aPEDESTRIAN\",\"secMark\":3564,\"msgCnt\":26,\"id\":\"24779D7E\",\"position\":{\"latitude\":40.2397377,\"longitude\":-74.2761437},\"accuracy\":{\"semiMajor\":1.00,\"semiMinor\":1.00,\"orientation\":44.9951935489},\"speed\":0,\"heading\":8898}";
      assertEquals(expected , actualPsm.toString());
   }

}
