package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Tested;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class J2735EventDescriptionTest {
   @Tested
   J2735EventDescription ed;

   @Test
   public void testGettersAndSetters() {
      List<Integer> description = new ArrayList<>();
      ed.setDescription(description);
      assertEquals(description, ed.getDescription());
      J2735Extent extent = null;
      ed.setExtent(extent);
      assertEquals(extent, ed.getExtent());
      J2735HeadingSlice heading = new J2735HeadingSlice();
      ed.setHeading(heading);
      assertEquals(heading, ed.getHeading());
      String priority = "";
      ed.setPriority(priority);
      assertEquals(priority, ed.getPriority());
      List<J2735RegionalContent> regional = new ArrayList<>();
      ed.setRegional(regional);
      assertEquals(regional, ed.getRegional());
      Integer typeEvent = 1;
      ed.setTypeEvent(typeEvent);
      assertEquals(typeEvent, ed.getTypeEvent());
   }
}
