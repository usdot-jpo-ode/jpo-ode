package us.dot.its.jpo.ode.traveler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Vector;

import org.junit.Test;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.VariableBinding;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.snmp.SnmpSession;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class TimControllerQueryTest {

   @Tested
   TimController testTimController;

   @Injectable
   OdeProperties mockOdeProperties;

   @Capturing
   MessageProducer<String, OdeObject> capturingMessageProducer;
   @Capturing
   JsonUtils capturingJsonUtils;
   @Capturing
   SnmpSession capturingSnmpSession;

   @Mocked
   Snmp mockSnmp;
   @Mocked
   ResponseEvent mockResponseEvent;
   @Mocked
   PDU mockPDU;

   @Test
   public void nullRequestShouldReturnError() {
      ResponseEntity<?> result = testTimController.bulkQuery(null);
      assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
      assertEquals("{\"error\":\"Empty request.\"}", result.getBody());
   }

   @Test
   public void emptyRequestShouldReturnError() {
      ResponseEntity<?> result = testTimController.bulkQuery("");
      assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
      assertEquals("{\"error\":\"Empty request.\"}", result.getBody());
   }

   @Test
   public void snmpSessionExceptionShouldReturnError() {
      try {
         new Expectations() {
            {
               new SnmpSession((RSU) any);
               result = new IOException("testException123");
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception in expectations block: " + e);
      }

      ResponseEntity<String> actualResponse = testTimController.bulkQuery("testString");
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, actualResponse.getStatusCode());
      assertTrue(actualResponse.getBody().contains("Failed to create SNMP session."));
   }

   @Test
   public void snmpSessionListenExceptionShouldReturnError() {
      try {
         new Expectations() {
            {
               capturingSnmpSession.startListen();
               result = new IOException("testException123");
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception in expectations block: " + e);
      }

      ResponseEntity<String> actualResponse = testTimController.bulkQuery("testString");
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, actualResponse.getStatusCode());
      assertTrue(actualResponse.getBody().contains("Failed to create SNMP session."));
   }

   @Test
   public void testNullResponseReturnsTimeout() throws IOException {
      new Expectations() {
         {
            mockOdeProperties.getRsuSrmSlots();
            result = 1;

            capturingSnmpSession.getSnmp();
            result = mockSnmp;

            mockSnmp.send((PDU) any, (UserTarget) any);
            result = null;
         }
      };

      ResponseEntity<String> actualResponse = testTimController.bulkQuery("testString");
      assertEquals(HttpStatus.BAD_REQUEST, actualResponse.getStatusCode());
      assertTrue(actualResponse.getBody().contains("Timeout, no response from RSU."));
   }

   @Test
   public void testNullResponseResponseReturnsTimeout() throws IOException {
      new Expectations() {
         {
            mockOdeProperties.getRsuSrmSlots();
            result = 1;

            capturingSnmpSession.getSnmp();
            result = mockSnmp;

            mockSnmp.send((PDU) any, (UserTarget) any);
            result = mockResponseEvent;

            mockResponseEvent.getResponse();
            result = null;
         }
      };

      ResponseEntity<String> actualResponse = testTimController.bulkQuery("testString");
      assertEquals(HttpStatus.BAD_REQUEST, actualResponse.getStatusCode());
      assertTrue(actualResponse.getBody().contains("Timeout, no response from RSU."));
   }
   
   @SuppressWarnings({ "rawtypes", "unchecked" })
   @Test
   public void testSuccessfulQuery() throws IOException {
      new Expectations() {
         {
            mockOdeProperties.getRsuSrmSlots();
            result = 1;

            capturingSnmpSession.getSnmp();
            result = mockSnmp;

            mockSnmp.send((PDU) any, (UserTarget) any);
            result = mockResponseEvent;

            mockResponseEvent.getResponse();
            result = mockPDU;
            
            mockPDU.getVariableBindings();
            result = new Vector<VariableBinding>();
         }
      };

      ResponseEntity<String> actualResponse = testTimController.bulkQuery("testString");
      assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
      assertTrue(actualResponse.getBody().contains("indicies_set"));
   }
   
   @Test
   public void testSuccessfulPopulatedQuery() throws IOException {
      new Expectations() {
         {
            mockOdeProperties.getRsuSrmSlots();
            result = 1;

            capturingSnmpSession.getSnmp();
            result = mockSnmp;

            mockSnmp.send((PDU) any, (UserTarget) any);
            result = mockResponseEvent;

            mockResponseEvent.getResponse();
            result = mockPDU;
            
            Vector<VariableBinding> fakeVector = new Vector<VariableBinding>();
            fakeVector.add(new VariableBinding());
            
            mockPDU.getVariableBindings();
            result = fakeVector;
         }
      };

      ResponseEntity<String> actualResponse = testTimController.bulkQuery("testString");
      assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
      assertTrue(actualResponse.getBody().contains("indicies_set"));
   }

}
