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
package us.dot.its.jpo.ode.traveler;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import mockit.Expectations;
import mockit.Injectable;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;

public class TimControllerHelperTest {

  @Injectable
  OdeProperties injectableOdeProperties;

  @Test
  public void testGetRsu() throws IOException {
     new Expectations() {{
        injectableOdeProperties.getRsuUsername();
        result="v3user";
        
        injectableOdeProperties.getRsuPassword();
        result="password";
     }};

     RSU expected = new RSU("127.0.0.1", "v3user", "password", 1, 2000);

     //rsuUsername and rsuPassword are null
     RSU actual1 = new RSU("127.0.0.1", null, null, 1, 2000);
     TimControllerHelper.updateRsuCreds(actual1, injectableOdeProperties);
     assertEquals(expected, actual1);
     
     //rsuUsername and rsuPassword are not-null
     RSU actual2 = new RSU("127.0.0.1", "v3user", "password", 1, 2000);
     TimControllerHelper.updateRsuCreds(actual2, injectableOdeProperties);
     assertEquals(expected, actual2);

     //rsuUsername and rsuPassword are blank
     RSU actual3 = new RSU("127.0.0.1", "", "", 1, 2000);
     TimControllerHelper.updateRsuCreds(actual3, injectableOdeProperties);
     assertEquals(expected, actual3);
  }
  

}
