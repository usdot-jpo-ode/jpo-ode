package us.dot.its.jpo.ode.services.vsd;

import static org.junit.Assert.assertEquals;
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
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.wrapper.MessageProducer;
import us.dot.its.jpo.ode.wrapper.serdes.VehSitDataMessageSerializer;

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
   VehSitDataMessageSerializer capturingVehSitDataMessageSerializer;

   @Mocked
   JsonNode mockJsonNode;
   @Mocked
   J2735Bsm mockJ2735Bsm;
   @Mocked
   VehSitDataMessage mockVehSitDataMessage;

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
      assertNull(testBsmToVsdPackager.transform("testString123"));
   }

   @Test
   public void testTransformReturnsNullWhenBundleNull() {
      try {
         new Expectations() {
            {
               JsonUtils.getJsonNode(anyString, anyString).get(anyString);
               result = mockJsonNode;

               capturingObjectMapper.treeToValue(mockJsonNode, J2735Bsm.class);
               result = mockJ2735Bsm;

               capturingVsdBundler.addToVsdBundle(mockJ2735Bsm);
               result = null;
            }
         };
      } catch (JsonProcessingException e) {
         fail("Unexpected exception in expectations block: " + e);
      }
      assertNull(testBsmToVsdPackager.transform("testString123"));
   }

   @Test
   public void testTransformReturnsSerializedVSD() {

      byte[] expectedByteArray = new byte[] { 4, 2, 3 };
      try {
         new Expectations() {
            {
               JsonUtils.getJsonNode(anyString, anyString).get(anyString);
               result = mockJsonNode;

               capturingObjectMapper.treeToValue(mockJsonNode, J2735Bsm.class);
               result = mockJ2735Bsm;

               capturingVsdBundler.addToVsdBundle(mockJ2735Bsm);
               result = mockVehSitDataMessage;

               capturingVehSitDataMessageSerializer.serialize(null, mockVehSitDataMessage);
               result = expectedByteArray;
            }
         };
      } catch (JsonProcessingException e) {
         fail("Unexpected exception in expectations block: " + e);
      }
      assertEquals(expectedByteArray, testBsmToVsdPackager.transform("testString123"));
   }

}
