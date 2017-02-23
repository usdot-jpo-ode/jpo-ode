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
      TravelerInformation travelerInfo = new TravelerInformation();
      try {
         travelerInfo.setMsgCnt(new MsgCount(Integer.parseInt(msgcnt)));
      }
      catch (RuntimeException e)
      {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }      
   }
   
   @Test
   public void checkUpperBoundMessageCount() {
      String msgcnt = "128";
      TravelerInformation travelerInfo = new TravelerInformation();
      try {
         travelerInfo.setMsgCnt(new MsgCount(Integer.parseInt(msgcnt)));
      }
      catch (RuntimeException e)
      {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }      
   }
   
   @Test
   public void shouldReturnEqualDataFrames() {
      TravelerDataFrameList dataFrames = new TravelerDataFrameList();
      int val = dataFrames.getSize();
      assertEquals(val,0);
   }
   //Begin tests for dataframes
   
   @Test
   public void checkSSPIndex() {
      TravelerDataFrame dataFrame = new TravelerDataFrame();
      int sspindex = 3;
      dataFrame.setSspTimRights(new SSPindex(sspindex));
      
      assertEquals(3,dataFrame.getSspTimRights().intValue());
   }
   
   @Test
   public void checkRoadsignID() {
      MsgId msgId = new MsgId();
      msgId.setChosenFlag(MsgId.roadSignID_chosen);
      assertEquals(msgId.getChosenFlag(), MsgId.roadSignID_chosen);
   }
   
   @Test
   public void checkFurtherInfoID() {
      MsgId msgId = new MsgId();
      msgId.setChosenFlag(MsgId.furtherInfoID_chosen);
      assertEquals(msgId.getChosenFlag(),MsgId.furtherInfoID_chosen);
   }
   
   @Test
   public void checkInfoType() {
      TravelerDataFrame df = new TravelerDataFrame();
      df.setFrameType(TravelerInfoType.valueOf(1));
      assertEquals(df.getFrameType(),TravelerInfoType.valueOf(1));
   }
   
   @Test
   public void checkLatitude() {
      //Position3D anchorPos = new Position3D(new Latitude(Short.parseShort(latitude)), new Longitude(Short.parseShort(longitude)));
   }
   
   @Test
   public void checkRegions() {
      TravelerDataFrame df = new TravelerDataFrame();
      Regions regions = new Regions();
      regions.add(new GeographicalPath());
      df.setRegions(regions);
   }
}
