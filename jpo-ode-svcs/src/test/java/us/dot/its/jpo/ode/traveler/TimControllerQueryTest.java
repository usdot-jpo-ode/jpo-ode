package us.dot.its.jpo.ode.traveler;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.snmp4j.event.ResponseEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import mockit.Capturing;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class TimControllerQueryTest {

   @Tested
   TimController testTimController;
   @Injectable
   OdeProperties mockOdeProperties;

   @Mocked
   ResponseEvent mockResponse;
   
   @Capturing
   MessageProducer<?,?> capturingMessageProducer;

   @Test
   public void shouldReturnFalseNullRequest() {
      ResponseEntity<?> result = testTimController.asyncQueryForTims(null);
      assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
      assertEquals("{\"error\":\"Empty request\"}", result.getBody());
   }
}
