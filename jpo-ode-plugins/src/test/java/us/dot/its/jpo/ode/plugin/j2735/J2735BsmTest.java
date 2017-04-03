package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.integration.junit4.JMockit;

import mockit.Tested;
@RunWith(JMockit.class)
public class J2735BsmTest {
   @Tested
   J2735Bsm b;
   @Test
   public void testGettersAndSetters() {
      J2735BsmCoreData coreData = new J2735BsmCoreData();
      b.setCoreData(coreData);
      assertEquals(coreData,b.getCoreData());
      List<J2735BsmPart2Content> partII = new ArrayList<>();
      b.setPartII(partII);
      assertEquals(partII,b.getPartII());
   }
}
