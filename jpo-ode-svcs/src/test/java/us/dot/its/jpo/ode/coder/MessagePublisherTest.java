package us.dot.its.jpo.ode.coder;

import java.text.ParseException;
import java.time.ZonedDateTime;

import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeMsgMetadata;
import us.dot.its.jpo.ode.util.DateTimeUtils;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class MessagePublisherTest {

   @Tested
   MessagePublisher testMessagePublisher;
   @Injectable
   OdeProperties testOdeProperties;
   @Mocked
   OdeBsmData testOdeBsmData;
   @Mocked
   OdeMsgMetadata testOdeMsgMetadata;
   @Capturing
   MessageProducer testMessageProducer;
   @Capturing
   DateTimeUtils testDateTimeUtils;
   @Mocked
   ZonedDateTime testZonedDateTime;

   @Test
   public void publishBothNull() {

      new Expectations() {
         {

            testOdeBsmData.getMetadata();
            result = null;
            testOdeBsmData.getMetadata().getReceivedAt();
            result = null;
         }
      };

      testMessagePublisher.publish(testOdeBsmData);

   }

   @Test
   public void publishBothNotNull() {

      String testString = "2011-12-03T10:15:30+01:00[Europe/Paris]";

      new Expectations() {
         {

            testOdeBsmData.getMetadata();
            result = testOdeMsgMetadata;
            testOdeBsmData.getMetadata().getReceivedAt();
            result = testString;
            testOdeBsmData.getMetadata().setLatency(anyLong); times = 1;
            
         }
      };

      testMessagePublisher.publish(testOdeBsmData);

   }
   
   @Test
   public void publishParseException() {
 String testString = "test";
      new Expectations() {
         {
           
            testOdeBsmData.getMetadata();
            result = testOdeMsgMetadata;
            testOdeBsmData.getMetadata().getReceivedAt();
            result = testString;
            DateTimeUtils.difference((ZonedDateTime)any, (ZonedDateTime)any);
            result = new ParseException(anyString, anyInt);
            
           // testOdeBsmData.getMetadata().setLatency(anyLong); times = 2;
         }
      };

      testMessagePublisher.publish(testOdeBsmData);

   }

   @Test
   public void publishNotNullStringAndNullMetadata() {

      String testString = "2011-12-03T10:15:30+01:00[Europe/Paris]";

      new Expectations() {
         {

            testOdeBsmData.getMetadata();
            result = null;
            testOdeBsmData.getMetadata().getReceivedAt();
            result = testString;
         }
      };

      testMessagePublisher.publish(testOdeBsmData);

   }
   
   @Test
   public void publishNotNullAndStringNull() {


      new Expectations() {
         {

            testOdeBsmData.getMetadata();
            result = testOdeMsgMetadata;
            testOdeBsmData.getMetadata().getReceivedAt();
            result = null;
         }
      };

      testMessagePublisher.publish(testOdeBsmData);

   }
}
