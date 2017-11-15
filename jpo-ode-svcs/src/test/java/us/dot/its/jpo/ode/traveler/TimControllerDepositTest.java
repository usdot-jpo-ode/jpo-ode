package us.dot.its.jpo.ode.traveler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Set;
import java.util.concurrent.Executors;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.model.OdeTravelerInputData;
import us.dot.its.jpo.ode.plugin.j2735.builders.TravelerMessageFromHumanToAsnConverter;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.JsonUtils.JsonUtilsException;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class TimControllerDepositTest {

   @Tested
   TimController testTimController;
   @Injectable
   OdeProperties mockOdeProperties;

   @Capturing
   Executors capturingExecutors;
   @Capturing
   MessageProducer<?, ?> capturingMessageProducer;
   @Capturing
   JsonUtils capturingJsonUtils;
   @Capturing
   TravelerMessageFromHumanToAsnConverter capturingTravelerMessageFromHumanToAsnConverter;

   @Mocked
   MessageProducer<String, String> mockStringMessageProducer;
   @Mocked
   MessageProducer<String, OdeObject> mockObjectMessageProducer;
   @Mocked
   OdeTravelerInputData mockTravelerInputData;

   @SuppressWarnings("unchecked")
   @Before
   public void setupMockMessageProducers() {
      new Expectations() {
         {
            MessageProducer.defaultStringMessageProducer(anyString, anyString, (Set<String>) any);
            result = mockStringMessageProducer;

            new MessageProducer<>(anyString, anyString, anyString, anyString, (Set<String>) any);
            result = mockObjectMessageProducer;
         }
      };
   }

   /**
    * Verify null and "" empty response are immediately caught
    */
   @Test
   public void testEmptyRequestsShouldReturnError() {

      try {
         ResponseEntity<String> response = testTimController.postTim(null);
         assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
         assertEquals("{\"error\":\"Empty request.\"}", response.getBody());
      } catch (Exception e) {
         fail("Unexpected exception " + e);
      }

      try {
         ResponseEntity<String> response = testTimController.postTim("");
         assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
         assertEquals("{\"error\":\"Empty request.\"}", response.getBody());
      } catch (Exception e) {
         fail("Unexpected exception " + e);
      }
   }

   /**
    * Throw and catch an exception in parsing the JSON.
    */
   @Test
   public void testMalformedJSONShouldReturnError() {

      new Expectations() {
         {
            JsonUtils.fromJson(anyString, (Class<?>) any);
            result = new NullPointerException("testNPEBadJson123");
         }
      };

      ResponseEntity<String> response = testTimController.postTim("test123");
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertEquals("{\"error\":\"Malformed or non-compliant JSON.\"}", response.getBody());
   }

   /**
    * The controller should send the TIM to the Kafka streams as long as they
    * pass the JSON parsing, even if the translation step fails.
    * 
    * @throws JsonUtilsException
    */
   @Test
   public void testPojosStillGetSentOnEncodingError() throws JsonUtilsException {
      new Expectations() {
         {
            JsonUtils.fromJson(anyString, (Class<?>) any);
            result = mockTravelerInputData;

            mockObjectMessageProducer.send(anyString, null, (OdeObject) any);
            times = 1;

            TravelerMessageFromHumanToAsnConverter.changeTravelerInformationToAsnValues((JsonNode) any);
            result = new JsonUtilsException("expectedException123", null);
         }
      };

      ResponseEntity<String> response = testTimController.postTim("test123");
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertEquals("{\"error\":\"Error converting to encodable TravelerInputData.\"}", response.getBody());
   }

   @Test
   public void testCatchExceptionOnXmlConversion() throws Exception {
      new Expectations() {
         @Mocked
         ObjectNode mockObjectNode;
         {
            JsonUtils.fromJson(anyString, (Class<?>) any);
            result = mockTravelerInputData;

            mockObjectMessageProducer.send(anyString, null, (OdeObject) any);
            times = 1;

            mockStringMessageProducer.send(anyString, null, anyString);
            times = 1;

            TravelerMessageFromHumanToAsnConverter.changeTravelerInformationToAsnValues((JsonNode) any);
            result = mockObjectNode;
         }
      };

      ResponseEntity<String> response = testTimController.postTim("test123");
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertEquals("{\"error\":\"Error sending data to ASN.1 Encoder module: testException123\"}", response.getBody());
   }
   
   @Test
   public void testGoodRequest() throws Exception {
      new Expectations() {
         @Mocked
         ObjectNode mockObjectNode;
         {
            JsonUtils.fromJson(anyString, (Class<?>) any);
            result = mockTravelerInputData;

            mockObjectMessageProducer.send(anyString, null, (OdeObject) any);
            times = 1;

            mockStringMessageProducer.send(anyString, null, anyString);
            times = 2;

            TravelerMessageFromHumanToAsnConverter.changeTravelerInformationToAsnValues((JsonNode) any);
            result = mockObjectNode;

            JsonUtils.jacksonFromJson(anyString, OdeTravelerInputData.class);
            result = mockTravelerInputData;

            XmlUtils.toXmlS((ObjectNode)any);
            result = "xmlString";
         }
      };

      ResponseEntity<String> response = testTimController.postTim("test123");
      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals("{\"success\":\"true\"}", response.getBody());
   }
}
