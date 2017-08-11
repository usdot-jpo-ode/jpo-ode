package us.dot.its.jpo.ode.services.vsd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;
import com.oss.asn1.PERUnalignedCoder;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class BsmToVsdPackagerTest {

   @Injectable
   MessageProducer<String, byte[]> injectableMessageProducer;

   @Injectable
   String injectableOutputTopic;

   @Capturing
   JsonUtils capturingJsonUtils;
   @Capturing
   J2735 capturingJ2735;
   @Capturing
   VsdBundler capturingVsdBundler;
   @Capturing
   ObjectMapper capturingObjectMapper;

   @Mocked
   J2735Bsm mockJ2735Bsm;
   @Mocked
   VsdBundler mockVsdBundler;
   @Mocked
   PERUnalignedCoder mockPERUnalignedCoder;

   @Test
   public void testMapperErrorReturnsEmptyArray() {
      try {
         new Expectations() {
            {
               J2735.getPERUnalignedCoder();
               times = 1;

               new VsdBundler();
               result = mockVsdBundler;

               capturingObjectMapper.treeToValue((JsonNode) any, (Class<?>) any);
               result = new IOException("testJsonException123");
            }
         };
      } catch (JsonProcessingException e) {
         fail("Unexpected exception in expectations block: " + e);
      }
      BsmToVsdPackager testBsmToVsdPackager = new BsmToVsdPackager(injectableMessageProducer, injectableOutputTopic);
      assertEquals(0, testBsmToVsdPackager.transform("this is a string").length);
   }

   @Test
   public void testBundlerReturnsVSD() {
      try {
         new Expectations() {

            {
               J2735.getPERUnalignedCoder();
               result = mockPERUnalignedCoder;

               mockPERUnalignedCoder.encode((VehSitDataMessage) any).array();
               result = new byte[] { 0 };

               new VsdBundler();
               result = mockVsdBundler;

               mockVsdBundler.addToVsdBundle((J2735Bsm) any);
               result = new VehSitDataMessage();

               capturingObjectMapper.treeToValue((JsonNode) any, (Class<?>) any);
               result = mockJ2735Bsm;
            }
         };
      } catch (EncodeFailedException | EncodeNotSupportedException | JsonProcessingException e) {
         fail("Unexpected exception in expectations block: " + e);
      }
      BsmToVsdPackager testBsmToVsdPackager = new BsmToVsdPackager(injectableMessageProducer, injectableOutputTopic);
      assertNotNull(testBsmToVsdPackager.transform("this also, is a string"));
   }

   @Test
   public void testBundlerReturnsEmptyArrayOnEncodingException(
         @Mocked EncodeNotSupportedException mockEncodeNotSupportedException) {
      try {
         new Expectations() {

            {
               J2735.getPERUnalignedCoder();
               result = mockPERUnalignedCoder;

               mockPERUnalignedCoder.encode((VehSitDataMessage) any);
               result = mockEncodeNotSupportedException;

               new VsdBundler();
               result = mockVsdBundler;

               mockVsdBundler.addToVsdBundle((J2735Bsm) any);
               result = new VehSitDataMessage();

               capturingObjectMapper.treeToValue((JsonNode) any, (Class<?>) any);
               result = mockJ2735Bsm;
            }
         };
      } catch (EncodeFailedException | EncodeNotSupportedException | JsonProcessingException e) {
         fail("Unexpected exception in expectations block: " + e);
      }
      BsmToVsdPackager testBsmToVsdPackager = new BsmToVsdPackager(injectableMessageProducer, injectableOutputTopic);
      assertEquals(0, testBsmToVsdPackager.transform("this also, is a string").length);
   }

}
