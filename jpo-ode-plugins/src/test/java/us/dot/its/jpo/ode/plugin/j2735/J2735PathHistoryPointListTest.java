package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Tested;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class J2735PathHistoryPointListTest {
   @Tested
   J2735PathHistoryPointList phpl;

   @Test
   public void testGettersAndSetters() {
      List<J2735PathHistoryPoint> pathHistoryPointList = new ArrayList<>();
      phpl.setPathHistoryPointList(pathHistoryPointList);
      assertEquals(pathHistoryPointList, phpl.getPathHistoryPointList());
   }
}
