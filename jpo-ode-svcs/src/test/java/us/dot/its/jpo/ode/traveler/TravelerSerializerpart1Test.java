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
      int msgcnt = -1;
      try {
         TravelerMessageBuilder.validateMessageCount(msgcnt);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e)
      {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }      
   }
   
   @Test
   public void checkLowerMessageCount() {
      int msgcnt = 0;
      try {
         TravelerMessageBuilder.validateMessageCount(msgcnt);
      }
      catch (RuntimeException e)
      {
         fail("Unexpected Exception");
      }      
   }
   
   @Test
   public void checkUpperBoundMessageCount() {
      int msgcnt = 128;
      try {
         TravelerMessageBuilder.validateMessageCount(msgcnt);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e)
      {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }     
   }
   
   @Test
   public void checkUpperMessageCount() {
      int msgcnt = 127;
      try {
         TravelerMessageBuilder.validateMessageCount(msgcnt);
      }
      catch (RuntimeException e)
      {
         fail("Unexpected Exception");
      }      
   }
   
   @Test
   public void checkLowerBoundSSPIndex() {
      short index = -1;
      try {
         TravelerMessageBuilder.validateHeaderIndex(index);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e)
      {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkLowerSSPIndex() {
      short index = 0;
      try {
         TravelerMessageBuilder.validateHeaderIndex(index);
      }
      catch (RuntimeException e)
      {
         fail("Unexpected Exception");
      }      
   }
   
   @Test
   public void checkUpperSSPIndex() {
      short index = 31;
      try {
         TravelerMessageBuilder.validateHeaderIndex(index);
      }
      catch (RuntimeException e)
      {
         fail("Unexpected Exception");
      }      
   }
   
   @Test
   public void checkUpperBoundSSPIndex() {
      short index = 32;
      try {
         TravelerMessageBuilder.validateHeaderIndex(index);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e)
      {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkLowerBoundInfoType() {
      int type = -1;
      try {
         TravelerMessageBuilder.validateInfoType(type);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e)
      {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkLowerInfoType() {
      int type = 0;
      try {
         TravelerMessageBuilder.validateInfoType(type);
      }
      catch (RuntimeException e)
      {
         fail("Unexpected Exception");
      }
   }
   
   @Test
   public void checkLowerBoundLat() {
      long lat = -900000001;
      try {
         TravelerMessageBuilder.validateLat(lat);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e)
      {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkUpperBoundLat() {
      long lat = 900000002;
      try {
         TravelerMessageBuilder.validateLat(lat);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e)
      {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkLowerLat() {
      long lat = -900000000;
      try {
         TravelerMessageBuilder.validateLat(lat);
      }
      catch (RuntimeException e)
      {
         fail("Unexcpeted Exception");
      }
   }
   
   @Test
   public void checkUpperLat() {
      long lat = 900000001;
      try {
         TravelerMessageBuilder.validateLat(lat);
      }
      catch (RuntimeException e)
      {
         fail("Unexpected Exception");
      }
   }
   
   @Test
   public void checkLowerBoundLong() {
      long longg = -1800000000;
      try {
         TravelerMessageBuilder.validateLong(longg);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e)
      {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkUpperBoundLong() {
      long longg = 1800000002;
      try {
         TravelerMessageBuilder.validateLong(longg);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e)
      {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkLowerLong() {
      long longg = -1799999999;
      try {
         TravelerMessageBuilder.validateLong(longg);
      }
      catch (RuntimeException e)
      {
         fail("Unexcpeted Exception");
      }
   }
   
   @Test
   public void checkUpperLong() {
      long longg = 1800000001;
      try {
         TravelerMessageBuilder.validateLong(longg);
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
         TravelerMessageBuilder.validateHeading(head);
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
         TravelerMessageBuilder.validateHeading(head);
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
         TravelerMessageBuilder.validateHeading(head);
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
         TravelerMessageBuilder.validateMinuteYear(min);
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
         TravelerMessageBuilder.validateMinuteYear(min);
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
         TravelerMessageBuilder.validateMinuteYear(min);
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
         TravelerMessageBuilder.validateMinuteYear(min);
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
         TravelerMessageBuilder.validateMinutesDuration(dur);
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
         TravelerMessageBuilder.validateMinutesDuration(dur);
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
         TravelerMessageBuilder.validateMinutesDuration(dur);
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
         TravelerMessageBuilder.validateMinutesDuration(dur);
      }
      catch (RuntimeException e)
      {
         fail("Unexpected Exception");
      }      
   }
   
   @Test
   public void checkUpperBoundSign() {
      int sign = 8;
      try {
         TravelerMessageBuilder.validateSign(sign);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e)
      {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }      
   }
   
   @Test
   public void checkLowerBoundSign() {
      int sign = -1;
      try {
         TravelerMessageBuilder.validateSign(sign);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e)
      {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }      
   }
   
   @Test
   public void checkLowerSign() {
      int sign = 0;
      try {
         TravelerMessageBuilder.validateSign(sign);
      }
      catch (RuntimeException e)
      {
         fail("Unexpected Exception");
      }      
   }
   
   @Test
   public void checkUpperSign() {
      int sign = 7;
      try {
         TravelerMessageBuilder.validateSign(sign);
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
         TravelerMessageBuilder.validateITISCodes(code);
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
         TravelerMessageBuilder.validateITISCodes(code);
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
         TravelerMessageBuilder.validateITISCodes(code);
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
         TravelerMessageBuilder.validateITISCodes(code);
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
         TravelerMessageBuilder.validateString(str);
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
         TravelerMessageBuilder.validateString(str);
      }
      catch (RuntimeException e)
      {
         fail("Unexpected Exception");
      }
   }
}
