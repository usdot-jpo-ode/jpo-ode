package us.dot.its.jpo.ode.coder;

import java.text.ParseException;
import java.time.ZonedDateTime;

import org.junit.Ignore;
import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.OdeMsgMetadata;
import us.dot.its.jpo.ode.util.DateTimeUtils;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class MessagePublisherTest {

   @Tested
   MessagePublisher testMessagePublisher;
   @Injectable
   OdeProperties testOdeProperties;
   @Mocked
   OdeBsmData mockOdeBsmData;
   @Mocked
   OdeMsgMetadata mockOdeMsgMetadata;
   
   @Capturing
   MessageProducer<String, OdeData> capturingMessageProducer;
   @Capturing
   DateTimeUtils capturingDateTimeUtils;
   @Mocked
   ZonedDateTime capturingZonedDateTime;

   @Test
   public void publishBothNull() {

      new Expectations() {
         {

            mockOdeBsmData.getMetadata();
            result = null;
            mockOdeBsmData.getPayload().getData();
         }
      };

      testMessagePublisher.publish(mockOdeBsmData);

   }

   @Test
   public void publishBothNotNull() {

      String testString = "2011-12-03T10:15:30+01:00[Europe/Paris]";

      new Expectations() {
         {

            mockOdeBsmData.getMetadata();
            result = mockOdeMsgMetadata;
            mockOdeBsmData.getMetadata().getReceivedAt();
            result = testString;
            mockOdeBsmData.getMetadata().setLatency(anyLong); times = 1;
            
         }
      };

      testMessagePublisher.publish(mockOdeBsmData);

   }
   
   @Test @Ignore
   public void publishParseException() {
 String testString = "test";
      new Expectations() {
         {
           
            mockOdeBsmData.getMetadata();
            result = mockOdeBsmData;
            mockOdeBsmData.getMetadata().getReceivedAt();
            result = testString;
            DateTimeUtils.difference((ZonedDateTime)any, (ZonedDateTime)any);
            result = new ParseException(anyString, anyInt);
            
           // testOdeBsmData.getMetadata().setLatency(anyLong); times = 2;
         }
      };

      testMessagePublisher.publish(mockOdeBsmData);

   }

   @Test
   public void publishNotNullStringAndNullMetadata() {

      String testString = "2011-12-03T10:15:30+01:00[Europe/Paris]";

      new Expectations() {
         {

            mockOdeBsmData.getMetadata();
            result = null;
            mockOdeBsmData.getMetadata().getReceivedAt();
            result = testString;
         }
      };

      testMessagePublisher.publish(mockOdeBsmData);

   }
   
   @Test
   public void publishNotNullAndStringNull() {


      new Expectations() {
         {

            mockOdeBsmData.getMetadata();
            result = mockOdeMsgMetadata;
            mockOdeBsmData.getMetadata().getReceivedAt();
            result = null;
         }
      };

      testMessagePublisher.publish(mockOdeBsmData);

   }
}
