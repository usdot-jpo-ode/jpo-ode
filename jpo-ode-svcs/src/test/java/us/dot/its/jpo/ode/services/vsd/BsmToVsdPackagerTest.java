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
package us.dot.its.jpo.ode.services.vsd;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class BsmToVsdPackagerTest {

   @Tested
   BsmToVsdPackager testBsmToVsdPackager;

   @Injectable
   MessageProducer<String, byte[]> injectableMessageProducer;
   @Injectable
   String injectableOutputTopic = "testOutputTopic";

   @Capturing
   JsonUtils capturingJsonUtils;
   @Capturing
   ObjectMapper capturingObjectMapper;
   @Capturing
   VsdBundler capturingVsdBundler;
   @Capturing
   //TODO open-ode
//   VehSitDataMessageSerializer capturingVehSitDataMessageSerializer;

   @Mocked
   JsonNode mockJsonNode;
   @Mocked
   J2735Bsm mockJ2735Bsm;
   //TODO open-ode
//   @Mocked
//   VehSitDataMessage mockVehSitDataMessage;

   @Test
   public void testTransformReturnsNullOnMapperException() {
      try {
         new Expectations() {
            {
               JsonUtils.getJsonNode(anyString, anyString).get(anyString);
               result = mockJsonNode;

               capturingObjectMapper.treeToValue(mockJsonNode, J2735Bsm.class);
               result = new IOException("testException123");
            }
         };
      } catch (JsonProcessingException e) {
         fail("Unexpected exception in expectations block: " + e);
      }
      assertNull(testBsmToVsdPackager.process("testString123"));
   }

   //TODO open-ode
//   @Test
//   public void testTransformReturnsNullWhenBundleNull() {
//      try {
//         new Expectations() {
//            {
//               JsonUtils.getJsonNode(anyString, anyString).get(anyString);
//               result = mockJsonNode;
//
//               capturingObjectMapper.treeToValue(mockJsonNode, J2735Bsm.class);
//               result = mockJ2735Bsm;
//
//               capturingVsdBundler.addToVsdBundle(mockJ2735Bsm);
//               result = null;
//            }
//         };
//      } catch (JsonProcessingException e) {
//         fail("Unexpected exception in expectations block: " + e);
//      }
//      assertNull(testBsmToVsdPackager.process("testString123"));
//   }
//
//   @Test
//   public void testTransformReturnsSerializedVSD() {
//
//      byte[] expectedByteArray = new byte[] { 4, 2, 3 };
//      try {
//         new Expectations() {
//            {
//               JsonUtils.getJsonNode(anyString, anyString).get(anyString);
//               result = mockJsonNode;
//
//               capturingObjectMapper.treeToValue(mockJsonNode, J2735Bsm.class);
//               result = mockJ2735Bsm;
//
//               capturingVsdBundler.addToVsdBundle(mockJ2735Bsm);
//               result = mockVehSitDataMessage;
//
//               //TODO open-ode
////               capturingVehSitDataMessageSerializer.serialize(null, mockVehSitDataMessage);
//               result = expectedByteArray;
//            }
//         };
//      } catch (JsonProcessingException e) {
//         fail("Unexpected exception in expectations block: " + e);
//      }
//      assertEquals(expectedByteArray, testBsmToVsdPackager.process("testString123"));
//   }

}
