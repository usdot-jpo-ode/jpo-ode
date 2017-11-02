package us.dot.its.jpo.ode.traveler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.springframework.http.HttpStatus;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.snmp.SnmpSession;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class TimControllerDeleteTest {
   
   @Tested
   TimController testTimController;
   
   @Injectable
   OdeProperties injectableOdeProperties;
   
   @Capturing
   MessageProducer<?,?> capturingMessageProducer;
   
   @Capturing
   Executors capturingExecutors;
   
   @Capturing
   SnmpSession capturingSnmpSession;
   @Capturing
   JsonUtils capturingJsonUtils;
   
   @Mocked
   ResponseEvent mockResponseEvent;
   
   @Test
   public void deleteShouldReturnBadRequestWhenNull() {
      assertEquals(HttpStatus.BAD_REQUEST, testTimController.deleteTim(null, 42).getStatusCode());
   }

   @Test
   public void deleteShouldCatchSessionIOException() {
      try {
         new Expectations() {
            {
               new SnmpSession((RSU) any);
               result = new IOException("testException123");
               
               JsonUtils.fromJson(anyString, (Class<?>) any);
               result = null;
            }
         };
      } catch (IOException e) {
         fail("Unexpected Exception in expectations block: " + e);
      }
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, testTimController.deleteTim("testJsonString", 42).getStatusCode());
   }

   @Test
   public void deleteShouldCatchSessionNullPointerException() {
      try {
         new Expectations() {
            {
               new SnmpSession((RSU) any);
               result = new NullPointerException("testException123");
               
               JsonUtils.fromJson(anyString, (Class<?>) any);
               result = null;
            }
         };
      } catch (IOException e) {
         fail("Unexpected Exception in expectations block: " + e);
      }
      assertEquals(HttpStatus.BAD_REQUEST, testTimController.deleteTim("testJsonString", 42).getStatusCode());
   }
   
   @Test
   public void deleteShouldCatchSnmpSetException() throws IOException {
      new Expectations() {{
         capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
         result = new IOException("testSnmpException123");
      }};
      
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, testTimController.deleteTim("testJsonString", 42).getStatusCode());
   }
   
   @Test
   public void deleteTestTimeout1() throws IOException {
      new Expectations() {{
         capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
         result = null;
      }};
      
      assertEquals(HttpStatus.REQUEST_TIMEOUT, testTimController.deleteTim("testJsonString", 42).getStatusCode());
   }
   
   @Test
   public void deleteTestTimeout2() throws IOException {
      new Expectations() {{
         capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
         result = mockResponseEvent;
         
         mockResponseEvent.getResponse();
         result = null;
      }};
      
      assertEquals(HttpStatus.REQUEST_TIMEOUT, testTimController.deleteTim("testJsonString", 42).getStatusCode());
   }
   
   @Test
   public void deleteTestOK() throws IOException {
      new Expectations() {{
         capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
         result = mockResponseEvent;
         
         mockResponseEvent.getResponse().getErrorStatus();
         result = 0;
      }};
      
      assertEquals(HttpStatus.OK, testTimController.deleteTim("testJsonString", 42).getStatusCode());
   }
   
   @Test
   public void deleteTestMessageAlreadyExists() throws IOException {
      new Expectations() {{
         capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
         result = mockResponseEvent;
         
         mockResponseEvent.getResponse().getErrorStatus();
         result = 12;
      }};
      
      assertEquals(HttpStatus.BAD_REQUEST, testTimController.deleteTim("testJsonString", 42).getStatusCode());
   }
   
   @Test
   public void deleteTestInvalidIndex() throws IOException {
      new Expectations() {{
         capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
         result = mockResponseEvent;
         
         mockResponseEvent.getResponse().getErrorStatus();
         result = 10;
      }};
      
      assertEquals(HttpStatus.BAD_REQUEST, testTimController.deleteTim("testJsonString", 42).getStatusCode());
   }
   
   @Test
   public void deleteTestUnknownErrorCode() throws IOException {
      new Expectations() {{
         capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
         result = mockResponseEvent;
         
         mockResponseEvent.getResponse().getErrorStatus();
         result = 5;
      }};
      
      assertEquals(HttpStatus.BAD_REQUEST, testTimController.deleteTim("testJsonString", 42).getStatusCode());
   }

}
