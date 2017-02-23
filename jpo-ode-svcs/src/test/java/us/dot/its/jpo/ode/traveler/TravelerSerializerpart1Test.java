package us.dot.its.jpo.ode.traveler;

import static org.junit.Assert.*;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.GeographicalPath;
import us.dot.its.jpo.ode.j2735.dsrc.Latitude;
import us.dot.its.jpo.ode.j2735.dsrc.Longitude;
import us.dot.its.jpo.ode.j2735.dsrc.MsgCount;
import us.dot.its.jpo.ode.j2735.dsrc.Position3D;
import us.dot.its.jpo.ode.j2735.dsrc.SSPindex;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrame;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrameList;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerInfoType;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerInformation;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrame.MsgId;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrame.Regions;


/**
 * @author 583114
 *
 */
public class TravelerSerializerpart1Test {
   
   @Test
   public void checkLowerBoundMessageCount() {
      String msgcnt = "-1";
      try {
         TravelerSerializer.validateMessageCount(msgcnt);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e)
      {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }      
   }
   
   @Test
   public void checkLowerMessageCount() {
      String msgcnt = "0";
      try {
         TravelerSerializer.validateMessageCount(msgcnt);
      }
      catch (RuntimeException e)
      {
         fail("Unexpected Exception");
      }      
   }
   
   @Test
   public void checkUpperBoundMessageCount() {
      String msgcnt = "128";
      try {
         TravelerSerializer.validateMessageCount(msgcnt);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e)
      {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }     
   }
   
   @Test
   public void checkUpperMessageCount() {
      String msgcnt = "127";
      try {
         TravelerSerializer.validateMessageCount(msgcnt);
      }
      catch (RuntimeException e)
      {
         fail("Unexpected Exception");
      }      
   }
   
   @Test
   public void checkLowerBoundSSPIndex() {
      String index = "-1";
      try {
         TravelerSerializer.validateHeaderIndex(index);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e)
      {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkLowerSSPIndex() {
      String index = "0";
      try {
         TravelerSerializer.validateHeaderIndex(index);
      }
      catch (RuntimeException e)
      {
         fail("Unexpected Exception");
      }      
   }
   
   @Test
   public void checkUpperSSPIndex() {
      String index = "0";
      try {
         TravelerSerializer.validateHeaderIndex(index);
      }
      catch (RuntimeException e)
      {
         fail("Unexpected Exception");
      }      
   }
   
   @Test
   public void checkUpperBoundSSPIndex() {
      String index = "32";
      try {
         TravelerSerializer.validateHeaderIndex(index);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e)
      {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkLowerBoundInfoType() {
      String type = "-1";
      try {
         TravelerSerializer.validateInfoType(type);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e)
      {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkLowerInfoType() {
      String type = "0";
      try {
         TravelerSerializer.validateInfoType(type);
      }
      catch (RuntimeException e)
      {
         fail("Unexpected Exception");
      }
   }
   
   @Test
   public void checkLowerBoundLat() {
      String lat = "-900000001";
      try {
         TravelerSerializer.validateLat(lat);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e)
      {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkUpperBoundLat() {
      String lat = "900000002";
      try {
         TravelerSerializer.validateLat(lat);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e)
      {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkLowerLat() {
      String lat = "-900000000";
      try {
         TravelerSerializer.validateLat(lat);
      }
      catch (RuntimeException e)
      {
         fail("Unexcpeted Exception");
      }
   }
   
   @Test
   public void checkUpperLat() {
      String lat = "900000001";
      try {
         TravelerSerializer.validateLat(lat);
      }
      catch (RuntimeException e)
      {
         fail("Unexpected Exception");
      }
   }
   
   @Test
   public void checkLowerBoundLong() {
      String longg = "-1800000000";
      try {
         TravelerSerializer.validateLong(longg);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e)
      {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkUpperBoundLong() {
      String longg = "1800000002";
      try {
         TravelerSerializer.validateLong(longg);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e)
      {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkLowerLong() {
      String longg = "-1799999999";
      try {
         TravelerSerializer.validateLong(longg);
      }
      catch (RuntimeException e)
      {
         fail("Unexcpeted Exception");
      }
   }
   
   @Test
   public void checkUpperLong() {
      String longg = "1800000001";
      try {
         TravelerSerializer.validateLong(longg);
      }
      catch (RuntimeException e)
      {
         fail("Unexpected Exception");
      }
   }
   
   @Test
   public void checkUpperHeading() {
      String head = "100110110101010101011001";
      try {
         TravelerSerializer.validateHeading(head);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e)
      {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkLowerHeading() {
      String head = "01001010";
      try {
         TravelerSerializer.validateHeading(head);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e)
      {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkHeading() {
      String head = "1011011010101100";
      try {
         TravelerSerializer.validateHeading(head);
      }
      catch (RuntimeException e)
      {
         fail("Unexpected Exception");
      }
   }
   
   @Test
   public void checkLowerBoundMinute() {
      String min = "-1";
      try {
         TravelerSerializer.validateMinuteYear(min);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e)
      {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }      
   }
   
   @Test
   public void checkLowerMinute() {
      String min = "0";
      try {
         TravelerSerializer.validateMinuteYear(min);
      }
      catch (RuntimeException e)
      {
         fail("Unexpected Exception");
      }      
   }
   
   @Test
   public void checkUpperBoundMinute() {
      String min = "527041";
      try {
         TravelerSerializer.validateMinuteYear(min);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e)
      {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }     
   }
   
   @Test
   public void checkUpperMinute() {
      String min = "527040";
      try {
         TravelerSerializer.validateMinuteYear(min);
      }
      catch (RuntimeException e)
      {
         fail("Unexpected Exception");
      }      
   }
   
   @Test
   public void checkLowerBoundMinuteDuration() {
      String dur = "-1";
      try {
         TravelerSerializer.validateMinutesDuration(dur);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e)
      {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }      
   }
   
   @Test
   public void checkLowerMinuteDuration() {
      String dur = "0";
      try {
         TravelerSerializer.validateMinutesDuration(dur);
      }
      catch (RuntimeException e)
      {
         fail("Unexpected Exception");
      }      
   }
   
   @Test
   public void checkUpperBoundMinuteDuration() {
      String dur = "32001";
      try {
         TravelerSerializer.validateMinutesDuration(dur);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e)
      {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }     
   }
   
   @Test
   public void checkUpperMinuteDuration() {
      String dur = "32000";
      try {
         TravelerSerializer.validateMinutesDuration(dur);
      }
      catch (RuntimeException e)
      {
         fail("Unexpected Exception");
      }      
   }
   
   @Test
   public void checkUpperBoundSign() {
      String sign = "8";
      try {
         TravelerSerializer.validateSign(sign);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e)
      {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }      
   }
   
   @Test
   public void checkLowerBoundSign() {
      String sign = "-1";
      try {
         TravelerSerializer.validateSign(sign);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e)
      {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }      
   }
   
   @Test
   public void checkLowerSign() {
      String sign = "0";
      try {
         TravelerSerializer.validateSign(sign);
      }
      catch (RuntimeException e)
      {
         fail("Unexpected Exception");
      }      
   }
   
   @Test
   public void checkUpperSign() {
      String sign = "7";
      try {
         TravelerSerializer.validateSign(sign);
      }
      catch (RuntimeException e)
      {
         fail("Unexpected Exception");
      }     
   }
   
   @Test
   public void checkUpperBoundITISCodes() {
      String code = "65536";
      try {
         TravelerSerializer.validateITISCodes(code);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e)
      {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }      
   }
   
   @Test
   public void checkLowerBoundITISCodes() {
      String code = "-1";
      try {
         TravelerSerializer.validateITISCodes(code);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e)
      {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }      
   }
   
   @Test
   public void checkLowerITISCodes() {
      String code = "0";
      try {
         TravelerSerializer.validateITISCodes(code);
      }
      catch (RuntimeException e)
      {
         fail("Unexpected Exception");
      }      
   }
   
   @Test
   public void checkUpperITISCodes() {
      String code = "65535";
      try {
         TravelerSerializer.validateITISCodes(code);
      }
      catch (RuntimeException e)
      {
         fail("Unexpected Exception");
      }     
   }
   
   @Test
   public void checkEmptyString() {
      String str = "";
      try {
         TravelerSerializer.validateString(str);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e)
      {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkNonEmptyString() {
      String str = "a";
      try {
         TravelerSerializer.validateString(str);
      }
      catch (RuntimeException e)
      {
         fail("Unexpected Exception");
      }
   }
}
