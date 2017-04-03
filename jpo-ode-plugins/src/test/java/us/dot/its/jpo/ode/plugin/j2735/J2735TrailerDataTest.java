package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Tested;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class J2735TrailerDataTest {
   @Tested
   J2735TrailerData td;
   
   @Test
   public void testGettersAndSetters() {
      J2735PivotPointDescription connection = new J2735PivotPointDescription();
      td.setConnection(connection);
      assertEquals(connection,td.getConnection());
      Integer sspRights = 1;
      td.setSspRights(sspRights);
      assertEquals(sspRights,td.getSspRights());
      List<J2735TrailerUnitDescription> units = new ArrayList<>();
      td.setUnits(units);
      assertEquals(units,td.getUnits());
   }
}
