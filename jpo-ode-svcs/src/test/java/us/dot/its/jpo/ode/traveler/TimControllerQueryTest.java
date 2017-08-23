package us.dot.its.jpo.ode.traveler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Mocked;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.snmp.SnmpSession;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class TimControllerQueryTest {

   TimController testTimController;
   
   @Mocked
   OdeProperties mockOdeProperties;
   @Mocked
   ExecutorService mockExecutorService;
   
   @Capturing
   MessageProducer<String, OdeObject> capturingMessageProducer;
   @Capturing
   JsonUtils capturingJsonUtils;
   @Capturing
   SnmpSession capturingSnmpSession;
   @Capturing
   Executors capturingExecutors;
   
   @Before
   public void createTestTimController() {
      testTimController = new TimController(mockOdeProperties);
   }

   @Test
   public void nullRequestShouldReturnError() {
      ResponseEntity<?> result = testTimController.asyncQueryForTims(null);
      assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
      assertEquals("{\"error\":\"Empty request.\"}", result.getBody());
   }
   
   @Test
   public void emptyRequestShouldReturnError() {
      ResponseEntity<?> result = testTimController.asyncQueryForTims("");
      assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
      assertEquals("{\"error\":\"Empty request.\"}", result.getBody());
   }
   
   @Test
   public void snmpSessionExceptionShouldReturnError() {
      try {
         new Expectations() {{
            new SnmpSession((RSU) any);
            result = new IOException("testException123");
         }};
      } catch (IOException e) {
         fail("Unexpected exception in expectations block: " + e);
      }
      
      ResponseEntity<String> actualResponse = testTimController.asyncQueryForTims("testString");
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, actualResponse.getStatusCode());
      assertTrue(actualResponse.getBody().contains("Failed to create SNMP session."));
   }
   
   @Test
   public void snmpSessionListenExceptionShouldReturnError() {
      try {
         new Expectations() {{
            capturingSnmpSession.startListen();
            result = new IOException("testException123");
         }};
      } catch (IOException e) {
         fail("Unexpected exception in expectations block: " + e);
      }
      
      ResponseEntity<String> actualResponse = testTimController.asyncQueryForTims("testString");
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, actualResponse.getStatusCode());
      assertTrue(actualResponse.getBody().contains("Failed to create SNMP session."));
   }
   
   @Test
   public void testOneSuccessfulQuery() {
      new Expectations() {{
         mockOdeProperties.getRsuSrmSlots();
         result = 1;
         
         Executors.newFixedThreadPool(anyInt);
         result = mockExecutorService;
      }};
      
      assertEquals(HttpStatus.OK, testTimController.asyncQueryForTims("testString").getStatusCode());
   }
   
   @Test
   public void shouldStopThreadsOnInterruptException(@Mocked InterruptedException mockInterruptedException) {
      try {
         new Expectations() {{
            mockOdeProperties.getRsuSrmSlots();
            result = 1;
            
            Executors.newFixedThreadPool(anyInt);
            result = mockExecutorService;
            
            mockExecutorService.invokeAll((Collection) any);
            result = mockInterruptedException;
            
            mockExecutorService.shutdownNow();
            times = 1;
         }};
      } catch (InterruptedException e) {
         fail("Unexpected exception in expectations block: " + e);
      }
      
      assertEquals(HttpStatus.OK, testTimController.asyncQueryForTims("testString").getStatusCode());
   }
   
   @Test
   public void shouldCatchExceptionOnSnmpSessionClose(@Mocked InterruptedException mockInterruptedException) {
      try {
         new Expectations() {{
            mockOdeProperties.getRsuSrmSlots();
            result = 1;
            
            Executors.newFixedThreadPool(anyInt);
            result = mockExecutorService;
            
            capturingSnmpSession.endSession();
            result = new IOException("testException123");
         }};
      } catch (IOException e) {
         fail("Unexpected exception in expectations block: " + e);
      }
      
      assertEquals(HttpStatus.OK, testTimController.asyncQueryForTims("testString").getStatusCode());
   }
   
   
   
}
