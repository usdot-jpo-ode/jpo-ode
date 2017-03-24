package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Tested;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class J2735TrailerUnitDescriptionTest {
   @Tested
   J2735TrailerUnitDescription tud;

   @Test
   public void testGettersAndSetters() {
      J2735BumperHeights bh = new J2735BumperHeights();
      tud.setBumperHeights(bh);
      assertEquals(bh,tud.getBumperHeights());
      
      BigDecimal cog = new BigDecimal(1);
      tud.setCenterOfGravity(cog);
      assertEquals(cog,tud.getCenterOfGravity());
      
      List<J2735TrailerHistoryPoint> crumb = new ArrayList<>();
      tud.setCrumbData(crumb);
      assertEquals(crumb,tud.getCrumbData());
      
      BigDecimal eo = new BigDecimal(1);
      tud.setElevationOffset(eo);
      assertEquals(eo,tud.getElevationOffset());
      
      J2735PivotPointDescription fp = new J2735PivotPointDescription();
      tud.setFrontPivot(fp);
      assertEquals(fp,tud.getFrontPivot());
      
      BigDecimal height = new BigDecimal(1);
      tud.setHeight(height);
      assertEquals(height,tud.getHeight());
      
      int m = 1;
      tud.setMass(m);
      assertTrue(m == tud.getMass());
      
      J2735Node_XY node = new J2735Node_XY();
      tud.setPositionOffset(node);
      assertEquals(node,tud.getPositionOffset());
      
      J2735PivotPointDescription rp = new J2735PivotPointDescription();
      tud.setRearPivot(rp);
      assertEquals(rp,tud.getRearPivot());
      
      BigDecimal rw = new BigDecimal(1);
      tud.setRearWheelOffset(rw);
      assertEquals(rw,tud.getRearWheelOffset());
   }
}
