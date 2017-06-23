package us.dot.its.jpo.ode.traveler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.snmp.SnmpSession;

public class TimControllerQueryTest {

   @Tested
   TimController testTimController;
   @Injectable
   OdeProperties mockOdeProperties;

   @Mocked
   ResponseEvent mockResponse;

   @Test
   public void shouldReturnFalseNullRequest(@Mocked final LoggerFactory disabledLogger) {
      ResponseEntity<?> result = testTimController.queryForTims(null);
      assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
      assertEquals("Empty request", result.getBody());
   }

   @Test
   public void shouldReturnFalseInvalidRequest(@Mocked final LoggerFactory disabledLogger) {
      String incorrectJson = "{\"rsuTarget\":\"invalidTarget\", \"rsuUsername\": \"invalidUsername\"}";
      ResponseEntity<?> result = testTimController.queryForTims(incorrectJson);
      assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
      assertEquals("Malformed JSON", result.getBody());
   }

   @Test
   public void shouldReturnTimeout(@Mocked final LoggerFactory disabledLogger,
         @Mocked final SnmpSession mockSnmpSession) {

      String seeminglyValidJson = "{\"rsuTarget\": \"127.0.0.1\",\"rsuUsername\": \"user\",\"rsuPassword\": \"pass\",\"rsuRetries\": \"1\",\"rsuTimeout\": \"2000\"}";
      try {
         new Expectations() {
            {
               mockSnmpSession.get((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
               result = null;
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception in expectations block: " + e);
      }

      ResponseEntity<?> result = testTimController.queryForTims(seeminglyValidJson);

      assertEquals(HttpStatus.REQUEST_TIMEOUT, result.getStatusCode());
      assertEquals("No response from RSU.", result.getBody());
   }

}
