package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Tested;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class J2735SpeedProfileTest {
   @Tested
   J2735SpeedProfile sp;
   
   @Test
   public void testGettersAndSetters() {
      List<Integer> speedReports = new ArrayList<>();
      sp.setSpeedReports(speedReports);
      assertEquals(speedReports,sp.getSpeedReports());
   }
}
