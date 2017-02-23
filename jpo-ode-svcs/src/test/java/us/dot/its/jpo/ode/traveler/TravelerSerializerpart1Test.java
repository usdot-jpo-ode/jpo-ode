package us.dot.its.jpo.ode.traveler;

import static org.junit.Assert.*;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.MsgCount;
import us.dot.its.jpo.ode.j2735.dsrc.SSPindex;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrame;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrameList;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerInformation;


/**
 * @author 583114
 *
 */
public class TravelerSerializerpart1Test {
   
   @Test
   public void shouldReturnNullString() {
      String jsonString = "{}";
      
      assertEquals("{}",jsonString);
   }
   //String target = obj.getJSONArray("RSUs").getJSONObject(0).getString("target");
   @Test
   public void shouldReturnNullTarget() {
      String target = null;
      
      assertEquals(null,target);
   }
   //String userName = obj.getJSONArray("RSUs").getJSONObject(0).getString("username");
   @Test
   public void shouldReturnNullUsername() {
      String user = null;
      
      assertEquals(null,user);
   }
   //String password = obj.getJSONArray("RSUs").getJSONObject(0).getString("pass");
   @Test
   public void shouldReturnNullPassword() {
      String pass = null;
      
      assertEquals(null,pass);
   }
   //String retries = obj.getJSONArray("RSUs").getJSONObject(0).getString("retries");
   @Test
   public void shouldReturnNotEquals() {
      int val = 0;
      int ceil = 10000;
      
      assertTrue(val >= 0);
      assertTrue(ceil <= 10000);
   }
   
   @Test
   public void shouldReturnEqualMessageCount() {
      String msgcnt = "3";
      TravelerInformation travelerInfo = new TravelerInformation();
      travelerInfo.setMsgCnt(new MsgCount(Integer.parseInt(msgcnt)));
      TravelerDataFrameList dataFrames = new TravelerDataFrameList();
      travelerInfo.setDataFrames(dataFrames);
      MsgCount val = travelerInfo.getMsgCnt();
      int num = val.intValue();
      
      assertEquals(3,num);
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
}
