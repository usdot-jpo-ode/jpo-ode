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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import mockit.Capturing;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class TimDepositControllerTest {

   @Tested
   TimDepositController testTimDepositController;
   
   @Injectable
   OdeProperties injectableOdeProperties;
   @Injectable
   TimTransmogrifier injectableTimTransmogrifier;
   
   @Capturing
   MessageProducer capturingMessageProducer;
   

   @Test
   public void emptyRequestShouldReturnError() {

      try {
         ResponseEntity<String> response = testTimDepositController.postTim(null);
         assertEquals("{\"error\":\"Empty request.\"}", response.getBody());
      } catch (Exception e) {
         fail("Unexpected exception " + e);
      }

      try {
         ResponseEntity<String> response = testTimDepositController.postTim("");
         assertEquals("{\"error\":\"Empty request.\"}", response.getBody());
      } catch (Exception e) {
         fail("Unexpected exception " + e);
      }
   }

   @Test
   public void badRequestShouldThrowException() {

      try {
         ResponseEntity<String> response = testTimDepositController.postTim("test123");
         assertEquals("{\"error\":\"Malformed or non-compliant JSON.\"}", response.getBody());
      } catch (Exception e) {
         fail("Unexpected exception " + e);
      }

      new Verifications() {
         {
            EventLogger.logger.info(anyString);
         }
      };
   }
   
   /**
    * Verify null and "" empty response are immediately caught
    */
   @Test
   public void testEmptyRequestsShouldReturnError() {

      try {
         ResponseEntity<String> response = testTimDepositController.postTim(null);
         assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
         assertEquals("{\"error\":\"Empty request.\"}", response.getBody());
      } catch (Exception e) {
         fail("Unexpected exception " + e);
      }

      try {
         ResponseEntity<String> response = testTimDepositController.postTim("");
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
      ResponseEntity<String> response = testTimDepositController.postTim("test123");
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertEquals("{\"error\":\"Malformed or non-compliant JSON.\"}", response.getBody());
   }

}
